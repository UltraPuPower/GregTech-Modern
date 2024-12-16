package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import lombok.Setter;
import org.w3c.dom.Element;

import java.util.Map;

public class ColorRectTexture extends TransformTexture {

    @Setter
    public int color;

    public ColorRectTexture() {
        this(0x00000000f);
    }

    public ColorRectTexture(int color) {
        this.color = color;
    }

    public ColorRectTexture(Color color) {
        this.color = color.argb();
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width,
                                float height) {
        graphics.fill((int) x, (int) y, (int) (x + width), (int) (y + height), color);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "color", Color::parseAndPack, this::color);
    }
}
