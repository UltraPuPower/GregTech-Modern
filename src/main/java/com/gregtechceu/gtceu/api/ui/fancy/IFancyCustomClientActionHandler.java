package com.gregtechceu.gtceu.api.ui.fancy;

import net.minecraft.network.FriendlyByteBuf;

public interface IFancyCustomClientActionHandler {

    default void receiveMessage(int id, FriendlyByteBuf buffer) {}
}
