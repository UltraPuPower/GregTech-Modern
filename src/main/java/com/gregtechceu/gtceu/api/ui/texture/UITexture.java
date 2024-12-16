package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.w3c.dom.Element;

import java.util.Map;

public interface UITexture {

    default UITexture color(int color) {
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

    default void draw(UIGuiGraphics graphics, PositionedRectangle rectangle) {
        this.draw(graphics, 0, 0, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
    }

    void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height);

    default void updateTick() {}

    UITexture EMPTY = new UITexture() {

        @Override
        public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height) {}
    };

    UITexture MISSING_TEXTURE = new UITexture() {

        @Override
        public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height) {
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

    default void drawSubArea(UIGuiGraphics graphics, float x, float y, float width, float height, float drawnU,
                             float drawnV, float drawnWidth, float drawnHeight) {
        draw(graphics, 0, 0, x, y, width, height);
    }

    /**
     * Read the properties, and potentially children, of this
     * texture from the given XML element
     *
     * @param model    The UI model that's being instantiated,
     *                 used for creating child components
     * @param element  The XML element representing this component
     * @param children The child elements of the XML element representing
     *                 this component by tag name, without duplicates
     */
    default void parseProperties(UIModel model, Element element, Map<String, Element> children) {}
}
