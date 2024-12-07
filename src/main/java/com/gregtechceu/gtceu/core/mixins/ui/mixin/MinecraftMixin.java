package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.event.WindowEvent;
import com.gregtechceu.gtceu.api.ui.util.DisposableScreen;

import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;

import com.mojang.blaze3d.platform.Window;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Unique
    private final Set<DisposableScreen> gtceu$screensToDispose = new HashSet<>();

    @Shadow
    @Final
    private Window window;

    @Shadow
    @Nullable
    public Screen screen;

    @Inject(method = "resizeDisplay", at = @At("TAIL"))
    private void captureResize(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new WindowEvent.Resized((Minecraft) (Object) this, this.window));
    }

    @Inject(method = "setScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;removed()V"))
    private void captureSetScreen(Screen screen, CallbackInfo ci) {
        if (screen != null && this.screen instanceof DisposableScreen disposable) {
            this.gtceu$screensToDispose.add(disposable);
        } else if (screen == null) {
            if (this.screen instanceof DisposableScreen disposable) {
                this.gtceu$screensToDispose.add(disposable);
            }

            for (var disposable : this.gtceu$screensToDispose) {
                try {
                    disposable.dispose();
                } catch (Throwable error) {
                    var report = new CrashReport("Failed to dispose screen", error);
                    report.addCategory("Screen being disposed: ")
                            .setDetail("Screen class", disposable.getClass())
                            .setDetail("Screen being closed", this.screen)
                            .setDetail("Total screens to dispose", this.gtceu$screensToDispose.size());

                    throw new ReportedException(report);
                }
            }

            this.gtceu$screensToDispose.clear();
        }
    }
}
