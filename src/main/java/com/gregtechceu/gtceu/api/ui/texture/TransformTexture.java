package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import org.joml.Quaternionf;

public abstract class TransformTexture implements UITexture {

    protected float xOffset;
    protected float yOffset;
    protected float scale = 1;
    protected float rotation;

    public TransformTexture rotate(float degree) {
        rotation = degree;
        return this;
    }

    public TransformTexture scale(float scale) {
        this.scale = scale;
        return this;
    }

    public TransformTexture transform(float xOffset, float yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        return this;
    }

    protected void preDraw(UIGuiGraphics graphics, float x, float y, float width, float height) {
        graphics.pose().pushPose();
        graphics.pose().translate(xOffset, yOffset, 0);

        graphics.pose().translate(x + width / 2f, y + height / 2f, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.pose().mulPose(new Quaternionf().rotationXYZ(0, 0, (float) Math.toRadians(rotation)));
        graphics.pose().translate(-x + -width / 2f, -y + -height / 2f, 0);
    }


    protected void postDraw(UIGuiGraphics graphics, float x, float y, float width, float height) {
        graphics.pose().popPose();
    }

    @Override
    public final void draw(UIGuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        preDraw(graphics, x, y, width, height);
        drawInternal(graphics, mouseX, mouseY, x, y, width, height);
        postDraw(graphics, x, y, width, height);
    }

    @Override
    public final void drawSubArea(UIGuiGraphics graphics, int x, int y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        preDraw(graphics, x, y, width, height);
        drawSubAreaInternal(graphics, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        postDraw(graphics, x, y, width, height);
    }

    protected abstract void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height);

    protected void drawSubAreaInternal(UIGuiGraphics graphics, int x, int y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        drawInternal(graphics, 0, 0, x, y, width, height);
    }

}
