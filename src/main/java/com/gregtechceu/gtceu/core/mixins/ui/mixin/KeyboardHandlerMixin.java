package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.layers.Layers;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @SuppressWarnings("UnresolvedMixinReference") // the unmapped names are a requirement for injecting into lambdas.
    @WrapOperation(method = { "lambda$charTyped$6", "lambda$charTyped$7", "m_90907_", "m_90903_" },
                   at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(CI)Z"))
    private static boolean captureScreenCharTyped(Screen screen, char character, int modifiers,
                                                  Operation<Boolean> original) {
        boolean handled = false;
        for (var instance : Layers.getInstances(screen)) {
            handled = instance.adapter.charTyped(character, modifiers);
            if (handled) break;
        }

        return handled || original.call(screen, character, modifiers);
    }
}
