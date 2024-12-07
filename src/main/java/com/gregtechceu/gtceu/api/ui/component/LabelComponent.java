package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.Observable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LabelComponent extends BaseUIComponent {

    protected final Font textRenderer = Minecraft.getInstance().font;

    protected Component text;
    protected List<FormattedCharSequence> wrappedText;

    protected VerticalAlignment verticalTextAlignment = VerticalAlignment.TOP;
    protected HorizontalAlignment horizontalTextAlignment = HorizontalAlignment.LEFT;

    protected final AnimatableProperty<Color> color = AnimatableProperty.of(Color.WHITE);
    protected final Observable<Integer> lineHeight = Observable.of(this.textRenderer.lineHeight);
    protected boolean shadow;
    protected int maxWidth;

    protected Function<Style, Boolean> textClickHandler = UIGuiGraphics.utilityScreen()::handleComponentClicked;

    protected LabelComponent(Component text) {
        this.text = text;
        this.wrappedText = new ArrayList<>();

        this.shadow = false;
        this.maxWidth = Integer.MAX_VALUE;

        this.lineHeight.observe($ -> this.notifyParentIfMounted());
    }

    public LabelComponent text(Component text) {
        this.text = text;
        this.notifyParentIfMounted();
        return this;
    }

    public Component text() {
        return this.text;
    }

    public LabelComponent maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        this.notifyParentIfMounted();
        return this;
    }

    public int maxWidth() {
        return this.maxWidth;
    }

    public LabelComponent shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public boolean shadow() {
        return this.shadow;
    }

    public LabelComponent color(Color color) {
        this.color.set(color);
        return this;
    }

    public AnimatableProperty<Color> color() {
        return this.color;
    }

    public LabelComponent verticalTextAlignment(VerticalAlignment verticalAlignment) {
        this.verticalTextAlignment = verticalAlignment;
        return this;
    }

    public VerticalAlignment verticalTextAlignment() {
        return this.verticalTextAlignment;
    }

    public LabelComponent horizontalTextAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalTextAlignment = horizontalAlignment;
        return this;
    }

    public HorizontalAlignment horizontalTextAlignment() {
        return this.horizontalTextAlignment;
    }

    public LabelComponent lineHeight(int lineHeight) {
        this.lineHeight.set(lineHeight);
        return this;
    }

    public int lineHeight() {
        return this.lineHeight.get();
    }

    public LabelComponent textClickHandler(Function<Style, Boolean> textClickHandler) {
        this.textClickHandler = textClickHandler;
        return this;
    }

    public Function<Style, Boolean> textClickHandler() {
        return textClickHandler;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        int widestText = 0;
        for (var line : this.wrappedText) {
            int width = this.textRenderer.width(line);
            if (width > widestText) widestText = width;
        }

        if (widestText > this.maxWidth) {
            this.wrapLines();
            return this.determineHorizontalContentSize(sizing);
        } else {
            return widestText;
        }
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        this.wrapLines();
        return (this.wrappedText.size() * (this.lineHeight() + 2)) - 2;
    }

    @Override
    public void inflate(Size space) {
        this.wrapLines();
        super.inflate(space);
    }

    private void wrapLines() {
        this.wrappedText = this.textRenderer.split(this.text, this.horizontalSizing.get().isContent() ? this.maxWidth : this.width);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.color.update(delta);
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        var pose = graphics.pose();

        pose.pushPose();
        pose.translate(0, 1 / Minecraft.getInstance().getWindow().getGuiScale(), 0);

        int x = this.x;
        int y = this.y;

        if (this.horizontalSizing.get().isContent()) {
            x += this.horizontalSizing.get().value;
        }
        if (this.verticalSizing.get().isContent()) {
            y += this.verticalSizing.get().value;
        }

        switch (this.verticalTextAlignment) {
            case CENTER -> y += (this.height - ((this.wrappedText.size() * (this.lineHeight() + 2)) - 2)) / 2;
            case BOTTOM -> y += this.height - ((this.wrappedText.size() * (this.lineHeight() + 2)) - 2);
        }

        final int lambdaX = x;
        final int lambdaY = y;

        graphics.drawManaged(() -> {
            for (int i = 0; i < this.wrappedText.size(); i++) {
                var renderText = this.wrappedText.get(i);
                int renderX = lambdaX;

                switch (this.horizontalTextAlignment) {
                    case CENTER -> renderX += (this.width - this.textRenderer.width(renderText)) / 2;
                    case RIGHT -> renderX += this.width - this.textRenderer.width(renderText);
                }

                int renderY = lambdaY + i * (this.lineHeight() + 2);
                renderY += this.lineHeight() - this.textRenderer.lineHeight;

                graphics.drawString(this.textRenderer, renderText, renderX, renderY, this.color.get().argb(), this.shadow);
            }
        });

        pose.popPose();
    }

    @Override
    public void drawTooltip(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.drawTooltip(graphics, mouseX, mouseY, partialTicks, delta);

        if (!this.isInBoundingBox(mouseX, mouseY)) return;
        graphics.renderComponentHoverEffect(this.textRenderer, this.styleAt(mouseX - this.x, mouseY - this.y), mouseX, mouseY);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.textClickHandler.apply(this.styleAt((int) mouseX, (int) mouseY)) | super.onMouseDown(mouseX, mouseY, button);
    }

    protected Style styleAt(int mouseX, int mouseY) {
        return this.textRenderer.getSplitter().componentStyleAtWidth(this.wrappedText.get(Math.min(mouseY / (this.lineHeight() + 2), this.wrappedText.size() - 1)), mouseX);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "text", UIParsing::parseText, this::text);
        UIParsing.apply(children, "max-width", UIParsing::parseUnsignedInt, this::maxWidth);
        UIParsing.apply(children, "color", Color::parse, this::color);
        UIParsing.apply(children, "shadow", UIParsing::parseBool, this::shadow);
        UIParsing.apply(children, "line-height", UIParsing::parseUnsignedInt, this::lineHeight);

        UIParsing.apply(children, "vertical-text-alignment", VerticalAlignment::parse, this::verticalTextAlignment);
        UIParsing.apply(children, "horizontal-text-alignment", HorizontalAlignment::parse, this::horizontalTextAlignment);
    }
}
