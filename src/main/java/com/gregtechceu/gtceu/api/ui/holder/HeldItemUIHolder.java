package com.gregtechceu.gtceu.api.ui.holder;

import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import lombok.Getter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HeldItemUIHolder implements IUIHolder {

    private final Player player;
    @Getter
    private final InteractionHand hand;
    @Getter
    private final ItemStack held;

    public HeldItemUIHolder(Player player, InteractionHand hand) {
        this.player = player;
        this.hand = hand;
        this.held = player.getItemInHand(hand);
    }

    @Override
    public void loadUITemplate(Player entityPlayer, RootContainer rootComponent) {
        if (held.getItem() instanceof IHeldItemUIConstructor itemUIHolder) {
            itemUIHolder.loadUITemplate(player, rootComponent, this);
        }
    }

    @Override
    public boolean isInvalid() {
        return !ItemStack.isSameItemSameTags(player.getItemInHand(hand), held);
    }

    @Override
    public boolean isClientSide() {
        return player.level().isClientSide;
    }

    @Override
    public void markDirty() {

    }

    public interface IHeldItemUIConstructor {

        void loadUITemplate(Player entityPlayer, RootContainer rootComponent, HeldItemUIHolder holder);
    }
}
