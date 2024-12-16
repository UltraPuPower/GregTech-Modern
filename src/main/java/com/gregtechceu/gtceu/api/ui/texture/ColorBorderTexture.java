package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.w3c.dom.Element;

@Accessors(fluent = true, chain = true)
public class ColorBorderTexture extends TransformTexture {

    @Setter
    public Color color;
    @Setter
    public int border;

    protected ColorBorderTexture(Color color, int border) {
        this.color = color;
        this.border = border;
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width,
                                float height) {
        if (width == 0 || height == 0) return;
        final int argb = color.argb();
        graphics.drawManaged(() -> {
            graphics.drawSolidRect(x - border, y - border, width + 2 * border, border, argb);
            graphics.drawSolidRect(x - border, y - border, width + 2 * border, border, argb);
            graphics.drawSolidRect(x - border, y + height, width + 2 * border, border, argb);
            graphics.drawSolidRect(x - border, y, border, height, argb);
            graphics.drawSolidRect(x + width, y, border, height, argb);
        });
    }

    public static ColorBorderTexture parse(Element element) {
        UIParsing.expectAttributes(element, "color", "border");
        Color color = Color.parse(element.getAttributeNode("color"));
        int border = UIParsing.parseSignedInt(element.getAttributeNode("border"));
        return new ColorBorderTexture(color, border);
    }
}
