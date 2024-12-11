package com.gregtechceu.gtceu.api.ui.fancy;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IFancyCustomMouseWheelAction extends IFancyCustomClientActionHandler {

    default boolean mouseWheelMove(BiConsumer<Integer, Consumer<FriendlyByteBuf>> writeClientAction, double mouseX,
                                   double mouseY, double wheelDelta) {
        return false;
    }
}
