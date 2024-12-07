package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.layers.Layers;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow
    private int activeButton;

    @SuppressWarnings("UnresolvedMixinReference") // the unmapped names are a requirement for injecting into lambdas.
    @Inject(method = { "lambda$onMove$11", "m_168072_" }, at = @At("HEAD"), cancellable = true)
    private void captureScreenMouseDrag(Screen screen, double mouseX, double mouseY, double deltaX, double deltaY,
                                        CallbackInfo ci) {
        boolean handled = false;
        for (var instance : Layers.getInstances(screen)) {
            handled = instance.adapter.mouseDragged(mouseX, mouseY, this.activeButton, deltaX, deltaY);
            if (handled) break;
        }

        if (handled) ci.cancel();
    }
}
