package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class ResourceTexture extends TransformTexture {
    
    public ResourceLocation imageLocation;
    public int offsetX = 0;
    public int offsetY = 0;
    public int imageWidth = 1;
    public int imageHeight = 1;
    protected int color = -1;

    public ResourceTexture(ResourceLocation imageLocation, int offsetX, int offsetY, int width, int height) {
        this.imageLocation = imageLocation;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public ResourceTexture(ResourceLocation imageLocation) {
        this(imageLocation, 0, 0, 1, 1);
    }

    public ResourceTexture getSubTexture(float offsetX, float offsetY, float width, float height) {
        return new ResourceTexture(imageLocation,
                (int) (this.offsetX + (imageWidth * offsetX)),
                (int) (this.offsetY + (imageHeight * offsetY)),
                (int) (this.imageWidth * width),
                (int) (this.imageHeight * height));
    }

    public ResourceTexture getSubTexture(double offsetX, double offsetY, double width, double height) {
        return new ResourceTexture(imageLocation,
                (int) (this.offsetX + (float)(imageWidth * offsetX)),
                (int) (this.offsetY + (float)(imageHeight * offsetY)),
                (int) (this.imageWidth * (float) width),
                (int) (this.imageHeight * (float)height));
    }

    public ResourceTexture copy() {
        return getSubTexture(0, 0, 1, 1);
    }

    @Override
    public ResourceTexture setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        drawSubArea(graphics, x, y, width, height, 0, 0, 1, 1);
    }

    @Override
    protected void drawSubAreaInternal(UIGuiGraphics graphics, int x, int y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        if (imageLocation == null || imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        float imageU = this.offsetX + (this.imageWidth * drawnU);
        float imageV = this.offsetY + (this.imageHeight * drawnV);
        int imageWidth = (int) (this.imageWidth * drawnWidth);
        int imageHeight = (int) (this.imageHeight * drawnHeight);

        graphics.blit(imageLocation, x, y, imageU, imageV, imageWidth, imageHeight, this.imageWidth, this.imageHeight);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, imageLocation);
        var matrix4f = graphics.pose().last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix4f, x, y + height, 0).uv(imageU, imageV + imageHeight).color(color).endVertex();
        bufferbuilder.vertex(matrix4f, x + width, y + height, 0).uv(imageU + imageWidth, imageV + imageHeight).color(color).endVertex();
        bufferbuilder.vertex(matrix4f, x + width, y, 0).uv(imageU + imageWidth, imageV).color(color).endVertex();
        bufferbuilder.vertex(matrix4f, x, y, 0).uv(imageU, imageV).color(color).endVertex();
        tesselator.end();
    }
}
