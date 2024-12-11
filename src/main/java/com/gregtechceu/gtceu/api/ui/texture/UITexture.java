package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

public interface UITexture {

    default UITexture setColor(int color) {
        return this;
    }

    default UITexture rotate(float degree) {
        return this;
    }

    default UITexture scale(float scale) {
        return this;
    }

    default UITexture transform(int xOffset, int yOffset) {
        return this;
    }


    void draw(UIGuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height);


    default void updateTick() {}

    UITexture EMPTY = new UITexture() {

        @Override
        public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {

        }
    };

    UITexture MISSING_TEXTURE = new UITexture() {
        
        @Override
        public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);
            var matrix4f = graphics.pose().last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, x, y + height, 0).uv(0, 1).endVertex();
            bufferbuilder.vertex(matrix4f, x + width, y + height, 0).uv(1, 1).endVertex();
            bufferbuilder.vertex(matrix4f, x + width, y, 0).uv(1, 0).endVertex();
            bufferbuilder.vertex(matrix4f, x, y, 0).uv(0, 0).endVertex();
            tesselator.end();
        }
    };
    
    default void drawSubArea(UIGuiGraphics graphics, int x, int y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        draw(graphics, 0, 0, x, y, width, height);
    }
}
