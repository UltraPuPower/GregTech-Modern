package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.util.pond.UIEntityRenderDispatcherExtension;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin implements UIEntityRenderDispatcherExtension {

    @Unique
    private boolean gtceu$showNametag = true;
    @Unique
    private boolean gtceu$counterRotate = false;

    @Override
    public void gtceu$setShowNametag(boolean showNametag) {
        this.gtceu$showNametag = showNametag;
    }

    @Override
    public boolean gtceu$showNametag() {
        return this.gtceu$showNametag;
    }

    @Override
    public void gtceu$setCounterRotate(boolean counterRotate) {
        this.gtceu$counterRotate = counterRotate;
    }

    @Override
    public boolean gtceu$counterRotate() {
        return this.gtceu$counterRotate;
    }

    @Shadow
    public Camera camera;

    @Inject(method = "renderFlame",
            at = @At(value = "INVOKE",
                     target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                     shift = At.Shift.AFTER))
    private void cancelFireRotation(PoseStack poseStack, MultiBufferSource buffer, Entity entity, CallbackInfo ci) {
        if (!this.gtceu$counterRotate) return;
        poseStack.mulPose(Axis.YP.rotationDegrees(this.camera.getYRot() + 170));
        poseStack.translate(0, 0, .1);
    }
}
