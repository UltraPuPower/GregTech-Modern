package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

    @Invoker("<init>")
    static GuiGraphics create(Minecraft client, PoseStack matrices,
                              MultiBufferSource.BufferSource vertexConsumerProvider) {
        return null;
    }

    @Invoker
    void callFlushIfUnmanaged();

    @Invoker("renderTooltipInternal")
    void gtceu$renderTooltipFromComponents(Font textRenderer, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner);

    @Accessor("bufferSource")
    MultiBufferSource.BufferSource gtceu$bufferSource();

    @Accessor("pose")
    PoseStack gtceu$getPose();

    @Mutable
    @Accessor("pose")
    void gtceu$setPose(PoseStack matrices);

    @Accessor("scissorStack")
    GuiGraphics.ScissorStack gtceu$getScissorStack();

    @Mutable
    @Accessor("scissorStack")
    void gtceu$setScissorStack(GuiGraphics.ScissorStack scissorStack);
}
