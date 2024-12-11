package com.gregtechceu.gtceu.client.ui;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.util.pond.UIAbstractContainerMenuExtension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

public class ScreenInternals {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ScreenInternals::afterScreenOpened);
        MinecraftForge.EVENT_BUS.addListener(UIGuiGraphics.UtilityScreen::onWindowResized);
    }

    public static void afterScreenOpened(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof MenuAccess<?> handled) {
            ((UIAbstractContainerMenuExtension) handled.getMenu())
                    .gtceu$attachToPlayer(Minecraft.getInstance().player);
        }
    }
}
