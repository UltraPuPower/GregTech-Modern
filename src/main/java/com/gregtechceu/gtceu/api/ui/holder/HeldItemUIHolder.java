package com.gregtechceu.gtceu.api.ui.holder;

import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;

import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeldItemUIHolder implements IUIHolder<HeldItemUIHolder> {

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
    public void loadServerUI(Player player, UIContainerMenu<HeldItemUIHolder> menu, HeldItemUIHolder holder) {
        if (held.getItem() instanceof IHeldItemUIConstructor itemUIHolder) {
            itemUIHolder.loadServerUI(this.player, menu, holder);
        }
    }

    @Override
    public void loadClientUI(Player player, UIAdapter<RootContainer> adapter) {
        if (held.getItem() instanceof IHeldItemUIConstructor itemUIHolder) {
            itemUIHolder.loadClientUI(this.player, adapter, this);
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
    public void markDirty() {}

    public interface IHeldItemUIConstructor {

        void loadServerUI(Player player, UIContainerMenu<HeldItemUIHolder> menu, HeldItemUIHolder holder);;

        @OnlyIn(Dist.CLIENT)
        void loadClientUI(Player entityPlayer, UIAdapter<RootContainer> adapter, HeldItemUIHolder holder);
    }
}
