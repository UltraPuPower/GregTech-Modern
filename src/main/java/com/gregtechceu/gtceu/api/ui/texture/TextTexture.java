package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Vector4f;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Accessors(fluent = true, chain = true)
public class TextTexture extends TransformTexture {

    @Setter
    public Supplier<Component> textSupplier;
    @Setter
    public Component text;

    @Setter
    public int color;

    @Setter
    public int backgroundColor;

    @Setter
    public int width;
    @Setter
    public float rollSpeed = 1;
    @Setter
    public boolean dropShadow = false;

    @Setter
    public TextType textType;

    private List<FormattedCharSequence> texts;

    public TextTexture(Component text, int color) {
        this.color = color;
        this.textType = TextType.NORMAL;
        this.text = text;
        texts = Collections.singletonList(this.text.getVisualOrderText());
    }

    public TextTexture(Component text) {
        this(text, -1);
    }

    public TextTexture maxWidth(int width) {
        this.width = width;
        if (LDLib.isClient()) {
            if (this.width > 0) {
                texts = Minecraft.getInstance().font.split(text, width);
                if (texts.isEmpty()) {
                    texts = Collections.singletonList(text.getVisualOrderText());
                }
            } else {
                texts = Collections.singletonList(text.getVisualOrderText());
            }
        }
        return this;
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height) {
        if (textSupplier != null) {
            this.text = textSupplier.get();
        }
        this.drawInternal(graphics, mouseX, mouseY, (int) x, (int) y, (int) width, (int) height);
    }

    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        updateTick();
        if (backgroundColor != 0) {
            graphics.fill(x, y, width, height, backgroundColor);
            RenderSystem.enableBlend();
        }
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        Font font = Minecraft.getInstance().font;
        int textH = font.lineHeight;
        if (textType == TextType.NORMAL) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                FormattedCharSequence line = texts.get(i);
                int lineWidth = font.width(line);
                int _x = x + (width - lineWidth) / 2;
                int _y = y + (height - textH) / 2 + i * font.lineHeight;
                graphics.drawString(font, line, _x, _y, color, dropShadow);
            }
        } else if (textType == TextType.LEFT) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                FormattedCharSequence line = texts.get(i);
                int _y = y + (height - textH) / 2 + i * font.lineHeight;
                graphics.drawString(font, line, x, _y, color, dropShadow);
            }
        } else if (textType == TextType.RIGHT) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                FormattedCharSequence line = texts.get(i);
                int lineWidth = font.width(line);
                int _y = y + (height - textH) / 2 + i * font.lineHeight;
                graphics.drawString(font, line, (x + width - lineWidth), _y, color, dropShadow);
            }
        } else if (textType == TextType.HIDE) {
            if (Widget.isMouseOver(x, y, width, height, mouseX, mouseY) && texts.size() > 1) {
                drawRollTextLine(graphics, x, y, width, height, font, textH, text);
            } else {
                FormattedCharSequence line = texts.size() > 1 ?
                        FormattedCharSequence.composite(texts.get(0), Component.literal("..").getVisualOrderText()) :
                        texts.get(0);
                drawTextLine(graphics, x, y, width, height, font, textH, line);
            }
        } else if (textType == TextType.ROLL || textType == TextType.ROLL_ALWAYS) {
            if (texts.size() > 1 && (textType == TextType.ROLL_ALWAYS || Widget.isMouseOver(x, y, width, height, mouseX, mouseY))) {
                drawRollTextLine(graphics, x, y, width, height, font, textH, text);
            } else {
                drawTextLine(graphics, x, y, width, height, font, textH, texts.get(0));
            }
        } else if (textType == TextType.LEFT_HIDE) {
            if (Widget.isMouseOver(x, y, width, height, mouseX, mouseY) && texts.size() > 1) {
                drawRollTextLine(graphics, x, y, width, height, font, textH, text);
            } else {
                String line = texts.get(0) + (texts.size() > 1 ? ".." : "");
                float _y = y + (height - textH) / 2f;
                graphics.drawString(font, line, x, _y, color, dropShadow);
            }
        } else if (textType == TextType.LEFT_ROLL || textType == TextType.LEFT_ROLL_ALWAYS) {
            if (texts.size() > 1 && (textType == TextType.LEFT_ROLL_ALWAYS || Widget.isMouseOver(x, y, width, height, mouseX, mouseY))) {
                drawRollTextLine(graphics, x, y, width, height, font, textH, text);
            } else {
                float _y = y + (height - textH) / 2f;
                graphics.drawString(font, texts.get(0), x, _y, color, dropShadow);
            }
        }
        graphics.pose().popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);

    }

    private void drawRollTextLine(GuiGraphics graphics, float x, float y, int width, int height, Font fontRenderer, int textH, Component line) {
        float _y = y + (height - textH) / 2f;
        int textW = fontRenderer.width(line);
        int totalW = width + textW + 10;
        float from = x + width;
        var trans = graphics.pose().last().pose();
        var realPos = trans.transform(new Vector4f(x, y, 0, 1));
        var realPos2 = trans.transform(new Vector4f(x + width, y + height, 0, 1));
        graphics.enableScissor((int) realPos.x, (int) realPos.y, (int) realPos2.x, (int) realPos2.y);
        var t = rollSpeed > 0 ? ((((rollSpeed * Math.abs((int) (System.currentTimeMillis() % 1000000)) / 10) % (totalW))) / (totalW)) : 0.5;
        graphics.drawString(fontRenderer, line, (int) (from - t * totalW), (int) _y, color, dropShadow);
        graphics.disableScissor();
    }

    private void drawTextLine(GuiGraphics graphics, float x, float y, int width, int height, Font fontRenderer, int textH, FormattedCharSequence line) {
        int textW = fontRenderer.width(line);
        float _x = x + (width - textW) / 2f;
        float _y = y + (height - textH) / 2f;
        graphics.drawString(fontRenderer, line, (int) _x, (int) _y, color, dropShadow);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.expectAttributes(element, "text");
        UIParsing.apply(children, "text", UIParsing::parseComponent, this::text);

        UIParsing.apply(children, "color", Color::parseAndPack, this::color);
        UIParsing.apply(children, "background-color", Color::parseAndPack, this::backgroundColor);
        UIParsing.apply(children, "max-width", UIParsing::parseUnsignedInt, this::width);
        UIParsing.apply(children, "roll-speed", UIParsing::parseFloat, this::rollSpeed);
        UIParsing.apply(children, "shadow", UIParsing::parseBool, this::dropShadow);
        UIParsing.apply(children, "text-type", UIParsing.parseEnum(TextType.class), this::textType);
    }

    public enum TextType {
        NORMAL,
        HIDE,
        ROLL,
        ROLL_ALWAYS,
        LEFT,
        RIGHT,
        LEFT_HIDE,
        LEFT_ROLL,
        LEFT_ROLL_ALWAYS
    }

}
