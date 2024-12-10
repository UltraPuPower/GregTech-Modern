package com.gregtechceu.gtceu.api.ui.core;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Consumer;

public interface UIComponentMenuAccess {

    void sendMessage(UIComponent component, int id, Consumer<FriendlyByteBuf> writer);

    AbstractContainerMenu container();
}
