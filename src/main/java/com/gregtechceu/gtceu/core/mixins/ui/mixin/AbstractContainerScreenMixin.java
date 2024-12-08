package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.util.pond.UISlotExtension;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

    @Shadow public abstract void onClose();

    @Unique
    private static boolean gtceu$inGTScreen = false;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "render", at = @At("HEAD"))
    private void captureOwoState(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        gtceu$inGTScreen = (Object) this instanceof BaseContainerScreen<?,?>;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void resetOwoState(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        gtceu$inGTScreen = false;
    }

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void injectSlotScissors(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
        if (!gtceu$inGTScreen) return;

        var scissorArea = ((UISlotExtension) slot).gtceu$getScissorArea();
        if (scissorArea == null) return;

        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox(scissorArea.x(), scissorArea.y(), scissorArea.width(), scissorArea.height());
    }

    @Inject(method = "renderSlot", at = @At("RETURN"))
    private void clearSlotScissors(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
        if (!gtceu$inGTScreen) return;

        var scissorArea = ((UISlotExtension) slot).gtceu$getScissorArea();
        if (scissorArea == null) return;

        GlStateManager._disableScissorTest();
    }

    @Inject(method = "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V", at = @At(value = "HEAD"))
    private static void enableSlotDepth(GuiGraphics graphics, int x, int y, int z, CallbackInfo ci) {
        if (!gtceu$inGTScreen) return;
        RenderSystem.enableDepthTest();
        graphics.pose().translate(0, 0, 300);
    }

    @Inject(method = "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V", at = @At("TAIL"))
    private static void clearSlotDepth(GuiGraphics graphics, int x, int y, int z, CallbackInfo ci) {
        if (!gtceu$inGTScreen) return;
        graphics.pose().translate(0, 0, -300);
    }

    @SuppressWarnings("ConstantValue")
    @ModifyVariable(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0), ordinal = 3)
    private int doNoThrow(int slotId, @Local() Slot slot) {
        return (((Object) this instanceof BaseContainerScreen<?,?>) && slot != null) ? slot.getSlotIndex() : slotId;
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;checkHotbarKeyPressed(II)Z"), cancellable = true)
    private void closeIt(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof BaseContainerScreen<?, ?>)) return;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.onClose();
            cir.setReturnValue(true);
        }
    }
}
