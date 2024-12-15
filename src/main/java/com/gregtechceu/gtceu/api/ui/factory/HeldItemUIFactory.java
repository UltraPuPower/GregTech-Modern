package com.gregtechceu.gtceu.api.ui.factory;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.holder.HeldItemUIHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeldItemUIFactory extends UIFactory<HeldItemUIHolder> {

    public static final HeldItemUIFactory INSTANCE = new HeldItemUIFactory();

    public HeldItemUIFactory() {
        super(GTCEu.id("held_item"));
    }

    public final boolean openUI(ServerPlayer player, InteractionHand hand) {
        return openUI(new HeldItemUIHolder(player, hand), player);
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<HeldItemUIHolder> menu, HeldItemUIHolder holder) {
        menu.getHolder().loadServerUI(player, menu, holder);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, HeldItemUIHolder holder) {
        holder.loadClientUI(player, adapter, holder);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected HeldItemUIHolder readHolderFromSyncData(FriendlyByteBuf syncData) {
        Player player = Minecraft.getInstance().player;
        return player == null ? null : new HeldItemUIHolder(player, syncData.readEnum(InteractionHand.class));
    }

    @Override
    protected void writeHolderToSyncData(FriendlyByteBuf syncData, HeldItemUIHolder holder) {
        syncData.writeEnum(holder.getHand());
    }

    @Override
    public Component getUITitle(HeldItemUIHolder holder, Player player) {
        return holder.getHeld().getHoverName();
    }
}
