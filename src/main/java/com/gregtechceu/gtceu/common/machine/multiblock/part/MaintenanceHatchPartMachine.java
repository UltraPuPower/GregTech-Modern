package com.gregtechceu.gtceu.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IInteractedMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.ButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.fancy.FancyMachineUIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MaintenanceHatchPartMachine extends TieredPartMachine
                                         implements IMachineLife, IMaintenanceMachine, IInteractedMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MaintenanceHatchPartMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);
    private static final float MAX_DURATION_MULTIPLIER = 1.1f;
    private static final float MIN_DURATION_MULTIPLIER = 0.9f;
    private static final float DURATION_ACTION_AMOUNT = 0.01f;

    @Getter
    private final boolean isConfigurable;
    @Persisted
    private final NotifiableItemStackHandler itemStackHandler;
    @Getter
    @Setter
    @Persisted
    @DescSynced
    @RequireRerender
    private boolean isTaped;
    @Getter
    @Setter
    @Persisted
    protected int timeActive;
    @Getter
    @Persisted
    @DescSynced
    protected byte maintenanceProblems = startProblems();
    @Getter
    @Persisted
    private float durationMultiplier = 1f;
    @Nullable
    protected TickableSubscription maintenanceSubs;

    public MaintenanceHatchPartMachine(IMachineBlockEntity metaTileEntityId, boolean isConfigurable) {
        super(metaTileEntityId, isConfigurable ? 3 : 1);
        this.isConfigurable = isConfigurable;
        this.itemStackHandler = createInventory();
        this.itemStackHandler.setFilter(itemStack -> itemStack.is(GTItems.DUCT_TAPE.get()));
    }

    //////////////////////////////////////
    // ****** Initialization ******//

    /// ///////////////////////////////////
    protected NotifiableItemStackHandler createInventory() {
        return new NotifiableItemStackHandler(this, 1, IO.BOTH, IO.BOTH);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onMachineRemoved() {
        clearInventory(itemStackHandler);
    }

    @Override
    public byte startProblems() {
        return ALL_PROBLEMS;
    }

    //////////////////////////////////////
    // ********* Logic **********//

    /// ///////////////////////////////////
    @Override
    public void setMaintenanceProblems(byte problems) {
        this.maintenanceProblems = problems;
        updateMaintenanceSubscription();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            updateMaintenanceSubscription();
        }
    }

    protected void updateMaintenanceSubscription() {
        if (hasMaintenanceProblems()) {
            maintenanceSubs = subscribeServerTick(maintenanceSubs, this::update);
        } else if (maintenanceSubs != null) {
            maintenanceSubs.unsubscribe();
            maintenanceSubs = null;
        }
    }

    public void update() {
        if (getOffsetTimer() % 20 == 0) {
            if (hasMaintenanceProblems()) {
                if (consumeDuctTape(this.itemStackHandler, 0)) {
                    fixAllMaintenanceProblems();
                    setTaped(true);
                }
            } else {
                updateMaintenanceSubscription();
            }
        }
    }

    /**
     * Fixes the maintenance problems of this hatch's Multiblock Controller
     *
     * @param entityPlayer the player performing the fixing
     */
    private void fixMaintenanceProblems(@Nullable Player entityPlayer) {
        if (!hasMaintenanceProblems())
            return;

        if (entityPlayer != null) {
            // Fix automatically on slot click by player in Creative Mode
            if (entityPlayer.isCreative()) {
                fixAllMaintenanceProblems();
                return;
            }
            // Then for every slot in the player's main inventory, try to duct tape fix
            for (int i = 0; i < entityPlayer.getInventory().items.size(); i++) {
                if (consumeDuctTape(new InvWrapper(entityPlayer.getInventory()), i)) {
                    fixAllMaintenanceProblems();
                    setTaped(true);
                    return;
                }
            }
            // Lastly for each problem the multi has, try to fix with tools
            fixProblemsWithTools(getMaintenanceProblems(), entityPlayer);
        }
    }

    /**
     * Handles duct taping for manual and auto-taping use
     *
     * @param handler is the handler to get duct tape from
     * @param slot    is the inventory slot to check for tape
     * @return true if tape was consumed, else false
     */
    private boolean consumeDuctTape(IItemHandler handler, int slot) {
        var stored = handler.getStackInSlot(slot);
        if (!stored.isEmpty() && stored.is(GTItems.DUCT_TAPE.get())) {
            return handler.extractItem(slot, 1, false).is(GTItems.DUCT_TAPE.get());
        }
        return false;
    }

    private boolean consumeDuctTape(Player player, InteractionHand hand) {
        var held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.is(GTItems.DUCT_TAPE.get())) {
            if (!player.isCreative()) {
                held.shrink(1);
            }
            return true;
        }
        return false;
    }

    /**
     * Attempts to fix a provided maintenance problem with a tool in the player's
     * inventory, if the tool exists.
     *
     * @param problems     Problem Flags
     * @param entityPlayer Target Player which their inventory would be scanned for tools to fix
     */
    private void fixProblemsWithTools(byte problems, Player entityPlayer) {
        List<GTToolType> toolsToMatch = Arrays.asList(new GTToolType[6]);
        boolean proceed = false;
        for (byte index = 0; index < 6; index++) {
            if (((problems >> index) & 1) == 0) {
                proceed = true;
                switch (index) {
                    case 0 -> toolsToMatch.set(0, GTToolType.WRENCH);
                    case 1 -> toolsToMatch.set(1, GTToolType.SCREWDRIVER);
                    case 2 -> toolsToMatch.set(2, GTToolType.SOFT_MALLET);
                    case 3 -> toolsToMatch.set(3, GTToolType.HARD_HAMMER);
                    case 4 -> toolsToMatch.set(4, GTToolType.WIRE_CUTTER);
                    case 5 -> toolsToMatch.set(5, GTToolType.CROWBAR);
                }
            }
        }
        if (!proceed) {
            return;
        }

        for (int i = 0; i < toolsToMatch.size(); i++) {
            GTToolType toolToMatch = toolsToMatch.get(i);
            if (toolToMatch != null) {
                // Try to use the item in the player's "hand" (under the cursor)
                ItemStack heldItem = entityPlayer.containerMenu.getCarried();
                if (ToolHelper.is(heldItem, toolToMatch)) {
                    fixProblemWithTool(i, heldItem, entityPlayer);

                    if (toolsToMatch.stream().allMatch(Objects::isNull)) {
                        return;
                    }
                }

                // Then try all the remaining inventory slots
                for (ItemStack itemStack : entityPlayer.getInventory().items) {
                    if (ToolHelper.is(itemStack, toolToMatch)) {
                        fixProblemWithTool(i, itemStack, entityPlayer);

                        if (toolsToMatch.stream().allMatch(Objects::isNull)) {
                            return;
                        }
                    }
                }

                if (entityPlayer instanceof ServerPlayer player) {
                    for (ItemStack stack : entityPlayer.getInventory().items) {
                        if (ToolHelper.is(stack, toolToMatch)) {
                            setMaintenanceFixed(i);
                            ToolHelper.damageItem(stack, player, 1);
                            if (toolsToMatch.stream().allMatch(Objects::isNull)) {
                                return;
                            }
                        }
                    }
                }

            }
        }
    }

    private void fixProblemWithTool(int problemIndex, ItemStack stack, Player player) {
        setMaintenanceFixed(problemIndex);
        if (player instanceof ServerPlayer serverPlayer) {
            ToolHelper.damageItem(stack, serverPlayer, 1);
        }
        setTaped(false);
    }

    /**
     * Fixes every maintenance problem of the controller
     */
    public void fixAllMaintenanceProblems() {
        for (int i = 0; i < 6; i++) setMaintenanceFixed(i);
    }

    @Override
    public boolean isFullAuto() {
        return false;
    }

    @Override
    public float getTimeMultiplier() {
        var result = 1f;
        if (durationMultiplier < 1.0)
            result = -20 * durationMultiplier + 21;
        else
            result = -8 * durationMultiplier + 9;
        return BigDecimal.valueOf(result)
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
    }

    //////////////////////////////////////
    // ******* INTERACTION *******//

    /// ///////////////////////////////////
    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                   BlockHitResult hit) {
        if (hasMaintenanceProblems()) {
            if (consumeDuctTape(player, hand)) {
                fixAllMaintenanceProblems();
                setTaped(true);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    //////////////////////////////////////
    // ******** GUI *********//

    /// ///////////////////////////////////
    @Override
    public ParentUIComponent createBaseUIComponent(FancyMachineUIComponent component) {
        StackLayout group;
        if (isConfigurable) {
            group = UIContainers.stack(Sizing.fixed(150), Sizing.fixed(70));
            group.padding(Insets.of(4, 8, 4, 8));
            group.child(UIContainers.verticalScroll(Sizing.fill(), Sizing.fill(),
                    UIComponents.componentPanel(list -> {
                        list.add(getTextWidgetText("duration", this::getDurationMultiplier));
                        list.add(getTextWidgetText("time", this::getTimeMultiplier));
                        var buttonText = Component.translatable("gtceu.maintenance.configurable_duration.modify");
                        buttonText.append(" ");
                        buttonText.append(ComponentPanelWidget.withButton(Component.literal("[-]"), "sub"));
                        buttonText.append(" ");
                        buttonText.append(ComponentPanelWidget.withButton(Component.literal("[+]"), "add"));
                        list.add(buttonText);
                    }).maxWidthLimit(150 - 8 - 8 - 4).clickHandler((componentData, clickData) -> {
                        if (!clickData.isClientSide) {
                            if (componentData.equals("sub")) {
                                durationMultiplier = Mth.clamp(durationMultiplier - DURATION_ACTION_AMOUNT,
                                        MIN_DURATION_MULTIPLIER, MAX_DURATION_MULTIPLIER);
                            } else if (componentData.equals("add")) {
                                durationMultiplier = Mth.clamp(durationMultiplier + DURATION_ACTION_AMOUNT,
                                        MIN_DURATION_MULTIPLIER, MAX_DURATION_MULTIPLIER);
                            }
                        }
                    }))
                    .padding(Insets.both(4, 5))
                    .surface(Surface.UI_DISPLAY));
        } else {
            group = UIContainers.stack(Sizing.fixed(8 + 18), Sizing.fixed(8 + 20 + 18));
            group.padding(Insets.of(4));
        }
        group.child(UIComponents.slot(itemStackHandler, 0)
                .backgroundTexture(UITextures.group(GuiTextures.SLOT, GuiTextures.DUCT_TAPE_OVERLAY))
                .tooltip(List.of(Component.translatable("gtceu.machine.maintenance_hatch_tape_slot.tooltip"))));
        group.child(UIComponents.button(Component.empty(), data -> fixMaintenanceProblems(group.player()))
                .renderer(ButtonComponent.Renderer.texture(GuiTextures.MAINTENANCE_BUTTON))
                .positioning(Positioning.absolute(0, 20))
                .tooltip(List.of(Component.translatable("gtceu.machine.maintenance_hatch_tool_slot.tooltip"))));
        group.surface(Surface.UI_BACKGROUND_INVERSE);
        return group;
    }

    private static Component getTextWidgetText(String type, Supplier<Float> multiplier) {
        Component tooltip;
        if (multiplier.get() == 1.0) {
            tooltip = Component.translatable("gtceu.maintenance.configurable_" + type + ".unchanged_description");
        } else {
            tooltip = Component.translatable("gtceu.maintenance.configurable_" + type + ".changed_description",
                    FormattingUtil.formatNumber2Places(multiplier.get()));
        }
        return Component
                .translatable("gtceu.maintenance.configurable_" + type,
                        FormattingUtil.formatNumber2Places(multiplier.get()))
                .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)));
    }
}
