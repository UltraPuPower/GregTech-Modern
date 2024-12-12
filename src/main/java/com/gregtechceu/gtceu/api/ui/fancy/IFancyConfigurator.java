package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2023/6/28
 * @implNote IFancyConfigurator
 */
public interface IFancyConfigurator {

    Component getTitle();

    UITexture getIcon();

    UIComponent createConfigurator();

    default List<Component> getTooltips() {
        return List.of(getTitle());
    }

    default void detectAndSendChange(BiConsumer<Integer, Consumer<FriendlyByteBuf>> sender) {}

    default void readUpdateInfo(int id, FriendlyByteBuf buf) {}

    default void writeInitialData(FriendlyByteBuf buffer) {}

    default void readInitialData(FriendlyByteBuf buffer) {}
}
