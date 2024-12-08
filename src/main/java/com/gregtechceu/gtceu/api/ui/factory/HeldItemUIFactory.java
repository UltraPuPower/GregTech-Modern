package com.gregtechceu.gtceu.api.ui.factory;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.holder.HeldItemUIHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class HeldItemUIFactory extends UIFactory<HeldItemUIHolder> {

    public static final HeldItemUIFactory INSTANCE = new HeldItemUIFactory();

    public HeldItemUIFactory() {
        super(GTCEu.id("held_item"));
    }

    public final boolean openUI(ServerPlayer player, InteractionHand hand) {
        return openUI(new HeldItemUIHolder(player, hand), player);
    }

    @Override
    public void loadUITemplate(Player player, RootContainer rootComponent, HeldItemUIHolder holder) {
        holder.loadUITemplate(player, rootComponent);
    }

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
