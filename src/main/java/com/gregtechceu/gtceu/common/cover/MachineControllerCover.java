package com.gregtechceu.gtceu.common.cover;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.cover.IUICover;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.*;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.common.cover.data.ControllerMode;
import com.gregtechceu.gtceu.data.lang.LangHandler;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MachineControllerCover extends CoverBehavior implements IUICover {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(MachineControllerCover.class,
            CoverBehavior.MANAGED_FIELD_HOLDER);
    private CustomItemStackHandler sideCoverSlot;
    private ButtonComponent modeButton;

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Persisted
    @Getter
    private boolean isInverted = false;

    @Persisted
    @Getter
    private int minRedstoneStrength = 1;

    @Persisted
    @DescSynced
    @Getter
    private ControllerMode controllerMode = ControllerMode.MACHINE;

    public MachineControllerCover(CoverDefinition definition, ICoverable coverHolder, Direction attachedSide) {
        super(definition, coverHolder, attachedSide);
    }

    @Override
    public boolean canAttach() {
        return !getAllowedModes().isEmpty();
    }

    @Override
    public void onAttached(ItemStack itemStack, ServerPlayer player) {
        super.onAttached(itemStack, player);

        setControllerMode(getAllowedModes().get(0));
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        resetCurrentControllable();
    }

    @Override
    public boolean canConnectRedstone() {
        return true;
    }

    @Override
    public void onNeighborChanged(Block block, BlockPos fromPos, boolean isMoving) {
        super.onNeighborChanged(block, fromPos, isMoving);

        updateInput();
    }

    public void setControllerMode(ControllerMode controllerMode) {
        resetCurrentControllable();

        this.controllerMode = controllerMode;
        updateAll();
    }

    public void setMinRedstoneStrength(int minRedstoneStrength) {
        this.minRedstoneStrength = minRedstoneStrength;
        updateAll();
    }

    public void setInverted(boolean inverted) {
        isInverted = inverted;
        updateAll();
    }

    private void updateAll() {
        updateInput();
        updateUI();
    }

    ///////////////////////////////////////////////////
    // *********** CONTROLLER LOGIC ***********//
    ///////////////////////////////////////////////////

    @Nullable
    private IControllable getControllable(@Nullable Direction side) {
        if (side == null) {
            return GTCapabilityHelper.getControllable(coverHolder.getLevel(), coverHolder.getPos(), null);
        }

        if (coverHolder.getCoverAtSide(side) instanceof IControllable cover) {
            return cover;
        } else {
            return null;
        }
    }

    private void updateInput() {
        if (controllerMode == null)
            return;

        IControllable controllable = getControllable(controllerMode.side);
        if (controllable != null) {
            controllable.setWorkingEnabled(shouldAllowWorking() && doOthersAllowWorking());
        }
    }

    private void resetCurrentControllable() {
        if (controllerMode == null)
            return;

        IControllable controllable = getControllable(controllerMode.side);
        if (controllable != null) {
            controllable.setWorkingEnabled(doOthersAllowWorking());
        }
    }

    private boolean shouldAllowWorking() {
        boolean shouldAllowWorking = getInputSignal() < minRedstoneStrength;

        return isInverted != shouldAllowWorking;
    }

    private boolean doOthersAllowWorking() {
        return coverHolder.getCovers().stream()
                .filter(cover -> this.attachedSide != cover.attachedSide)
                .filter(cover -> cover instanceof MachineControllerCover)
                .filter(cover -> ((MachineControllerCover) cover).controllerMode == this.controllerMode)
                .allMatch(cover -> ((MachineControllerCover) cover).shouldAllowWorking());
    }

    public List<ControllerMode> getAllowedModes() {
        return Arrays.stream(ControllerMode.values())
                .filter(mode -> mode.side != this.attachedSide)
                .filter(mode -> getControllable(mode.side) != null)
                .collect(Collectors.toList());
    }

    private int getInputSignal() {
        Level level = coverHolder.getLevel();
        BlockPos sourcePos = coverHolder.getPos().relative(attachedSide);

        return level.getSignal(sourcePos, attachedSide);
    }

    //////////////////////////////////////
    // *********** GUI ***********//
    //////////////////////////////////////

    @Override
    public ParentUIComponent createUIWidget(UIAdapter<StackLayout> adapter) {
        var group = UIContainers.stack(Sizing.fixed(176), Sizing.fixed(75));
        group.padding(Insets.both(0, 5));

        group.child(UIComponents.label(Component.translatable("cover.machine_controller.title"))
                .positioning(Positioning.absolute(0, 5)));
        group.child(new IntInputComponent(this::getMinRedstoneStrength, this::setMinRedstoneStrength)
                .setMin(1)
                .setMax(15)
                .positioning(Positioning.absolute(0, 20))
                .sizing(Sizing.fixed(131), Sizing.fixed(20)));

        modeButton = UIComponents.button(Component.empty(), cd -> selectNextMode())
                .renderer(ButtonComponent.Renderer.texture(GuiTextures.VANILLA_BUTTON))
                .configure(c -> {
                    c.positioning(Positioning.absolute(10, 45))
                            .sizing(Sizing.fixed(131), Sizing.fixed(20));
                });
        group.child(modeButton);

        // Inverted Mode Toggle:
        group.child(new ToggleButtonComponent(GuiTextures.INVERT_REDSTONE_BUTTON, this::isInverted, this::setInverted) {

            @Override
            public void update(float delta, int mouseX, int mouseY) {
                super.update(delta, mouseX, mouseY);
                tooltip(LangHandler.getMultiLang(
                        "cover.machine_controller.invert." + (pressed ? "enabled" : "disabled")));
            }
        }.positioning(Positioning.absolute(146, 20))
                .sizing(Sizing.fixed(20)));

        sideCoverSlot = new CustomItemStackHandler(1);
        group.child(new PhantomSlotComponent(sideCoverSlot, 0) {

            @Override
            public ItemStack slotClickPhantom(Slot slot, int mouseButton, ClickType clickTypeIn, ItemStack stackHeld) {
                return sideCoverSlot.getStackInSlot(0);
            }
        }.positioning(Positioning.absolute(147, 46)));

        updateUI();

        return group;
    }

    private void selectNextMode() {
        List<ControllerMode> allowedModes = getAllowedModes();

        setControllerMode(allowedModes.stream()
                .dropWhile(mode -> mode != this.controllerMode)
                .skip(1)
                .findFirst()
                .orElseGet(() -> allowedModes.get(0)));

        updateAll();
    }

    private void updateUI() {
        updateModeButton();
        updateCoverSlot();
    }

    private void updateModeButton() {
        if (modeButton == null) return;

        modeButton.renderer(ButtonComponent.Renderer.texture(UITextures.group(
                GuiTextures.VANILLA_BUTTON,
                UITextures.text(Component.translatable(controllerMode.localeName)))));
    }

    private void updateCoverSlot() {
        if (sideCoverSlot == null) return;

        Optional.ofNullable(controllerMode.side)
                .map(coverHolder::getCoverAtSide)
                .map(CoverBehavior::getAttachItem)
                .map(ItemStack::copy)
                .ifPresentOrElse(
                        item -> {
                            sideCoverSlot.setStackInSlot(0, item);
                            sideCoverSlot.onContentsChanged(0);
                        },
                        () -> {
                            sideCoverSlot.setStackInSlot(0, ItemStack.EMPTY);
                            sideCoverSlot.onContentsChanged(0);
                        });
    }
}
