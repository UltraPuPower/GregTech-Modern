package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joml.Quaternionf;
import org.w3c.dom.Element;

import java.util.Map;

@Accessors(fluent = true, chain = true)
public abstract class TransformTexture implements UITexture {

    @Setter
    protected float xOffset;
    @Setter
    protected float yOffset;
    @Setter
    protected float scale = 1;
    @Setter
    protected float rotation;

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
    public final void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height) {
        preDraw(graphics, x, y, width, height);
        drawInternal(graphics, mouseX, mouseY, x, y, width, height);
        postDraw(graphics, x, y, width, height);
    }

    @Override
    public final void drawSubArea(UIGuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        preDraw(graphics, x, y, width, height);
        drawSubAreaInternal(graphics, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        postDraw(graphics, x, y, width, height);
    }

    protected abstract void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height);

    protected void drawSubAreaInternal(UIGuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        drawInternal(graphics, 0, 0, x, y, width, height);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        UITexture.super.parseProperties(model, element, children);

        UIParsing.apply(children, "scale", UIParsing::parseFloat, this::scale);
        UIParsing.apply(children, "rotation", UIParsing::parseFloat, this::rotation);
        if (children.containsKey("offset")) {
            var offsetValues = UIParsing.childElements(children.get("offset"));
            UIParsing.apply(offsetValues, "x", UIParsing::parseFloat, this::xOffset);
            UIParsing.apply(offsetValues, "y", UIParsing::parseFloat, this::yOffset);
        }
    }

}
