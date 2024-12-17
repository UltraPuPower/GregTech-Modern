package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.ProspectingMapComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.holder.HeldItemUIHolder;
import com.gregtechceu.gtceu.api.ui.misc.ProspectorMode;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author KilaBash
 * @date 2023/7/10
 * @implNote ProspectorScannerBehavior
 */
public class ProspectorScannerBehavior implements IItemUIFactory, IInteractionItem, IAddInformation {

    private final int radius;
    private final long cost;
    private final ProspectorMode<?>[] modes;

    public ProspectorScannerBehavior(int radius, long cost, ProspectorMode<?>... modes) {
        this.radius = radius + 1;
        this.modes = Arrays.stream(modes).filter(Objects::nonNull).toArray(ProspectorMode[]::new);
        this.cost = cost;
    }

    @NotNull
    public ProspectorMode<?> getMode(ItemStack stack) {
        if (stack == ItemStack.EMPTY) {
            return modes[0];
        }
        var tag = stack.getTag();
        if (tag == null) {
            return modes[0];
        }
        return modes[tag.getInt("Mode") % modes.length];
    }

    public void setNextMode(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        tag.putInt("Mode", (tag.getInt("Mode") + 1) % modes.length);
    }

    public boolean drainEnergy(@NotNull ItemStack stack, boolean simulate) {
        IElectricItem electricItem = GTCapabilityHelper.getElectricItem(stack);
        if (electricItem == null) return false;

        int amount = Math.round(cost * (ConfigHolder.INSTANCE.machines.prospectorEnergyUseMultiplier / 100F));

        return electricItem.discharge(amount, Integer.MAX_VALUE, true, false, simulate) >= amount;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        ItemStack heldItem = player.getItemInHand(usedHand);
        if (player.isShiftKeyDown() && modes.length > 1) {
            if (!level.isClientSide) {
                setNextMode(heldItem);
                var mode = getMode(heldItem);
                player.sendSystemMessage(Component.translatable(mode.unlocalizedName));
            }
            return InteractionResultHolder.success(heldItem);
        }
        if (!player.isCreative() && !drainEnergy(heldItem, true)) {
            player.sendSystemMessage(Component.translatable("behavior.prospector.not_enough_energy"));
            return InteractionResultHolder.success(heldItem);
        }
        return IItemUIFactory.super.use(item, level, player, usedHand);
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<HeldItemUIHolder> menu, HeldItemUIHolder holder) {}

    @Override
    public void loadClientUI(Player entityPlayer, UIAdapter<StackLayout> adapter, HeldItemUIHolder holder) {
        FlowLayout flowLayout = UIContainers.horizontalFlow(Sizing.fixed(332), Sizing.fixed(200))
                .configure(c -> {
                    c.surface(Surface.UI_BACKGROUND)
                            .padding(Insets.of(4));
                });
        adapter.rootComponent.child(flowLayout);

        var mode = getMode(entityPlayer.getItemInHand(InteractionHand.MAIN_HAND));
        var map = new ProspectingMapComponent(Sizing.fill(), Sizing.fill(), radius, mode, 1);

        flowLayout.child(map)
                .child(UIComponents.switchComponent((cd, pressed) -> map.setDarkMode(pressed))
                        .supplier(map::isDarkMode)
                        .texture(
                                UITextures.group(GuiTextures.BUTTON,
                                        GuiTextures.PROGRESS_BAR_SOLAR_STEAM.get(true).copy()
                                                .getSubTexture(0, 0.5, 1, 0.5).scale(0.8f)),
                                UITextures.group(GuiTextures.BUTTON, GuiTextures.PROGRESS_BAR_SOLAR_STEAM.get(true)
                                        .copy().getSubTexture(0, 0, 1, 0.5).scale(0.8f)))
                        .positioning(Positioning.absolute(-20, 4))
                        .sizing(Sizing.fixed(18)));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("metaitem.prospector.tooltip.radius", radius));
        tooltipComponents.add(Component.translatable("metaitem.prospector.tooltip.modes"));
        for (ProspectorMode<?> mode : modes) {
            tooltipComponents.add(Component.literal(" -").append(Component.translatable(mode.unlocalizedName))
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        }
    }
}
