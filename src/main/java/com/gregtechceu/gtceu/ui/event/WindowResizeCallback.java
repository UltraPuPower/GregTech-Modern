package com.gregtechceu.gtceu.ui.event;

import appeng.api.events.EventFactory;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

public interface WindowResizeCallback {
    /*Event<WindowResizeCallback> EVENT = EventFactory.createArrayBacked(WindowResizeCallback.class, callbacks -> (client, window) -> {
        for (var callback : callbacks) {
            callback.onResized(client, window);
        }
    });*/

    /**
     * Called after the client's window has been resized
     *
     * @param client The currently active client
     * @param window The window which was resized
     */
    void onResized(Minecraft client, Window window);
}
