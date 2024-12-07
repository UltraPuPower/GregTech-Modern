package com.gregtechceu.gtceu.api.ui.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.Event;

import com.mojang.blaze3d.platform.Window;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class WindowEvent extends Event {

    @Getter
    private final Minecraft minecraft;
    @Getter
    private final Window window;

    /**
     * Called after the client's window has been resized
     */
    public static class Resized extends WindowEvent {

        /**
         * Called after the client's window has been resized
         *
         * @param minecraft The currently active client
         * @param window    The window which was resized
         */
        public Resized(Minecraft minecraft, Window window) {
            super(minecraft, window);
        }
    }
}
