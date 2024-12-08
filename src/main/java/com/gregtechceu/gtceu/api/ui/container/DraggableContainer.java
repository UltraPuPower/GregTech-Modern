package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.Map;

public class DraggableContainer<C extends UIComponent> extends WrappingParentUIComponent<C> {

    protected int foreheadSize = 10;
    protected boolean alwaysOnTop = false;

    protected int baseX = 0, baseY = 0;
    protected double xOffset = 0, yOffset = 0;

    protected DraggableContainer(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        super(horizontalSizing, verticalSizing, child);
        this.padding(Insets.none());
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.alwaysOnTop) graphics.pose().translate(0, 0, 500);
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        this.drawChildren(graphics, mouseX, mouseY, partialTicks, delta, this.childView);
        if (this.alwaysOnTop) graphics.pose().translate(0, 0, -500);
    }

    @Override
    public void drawTooltip(UIGuiGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.alwaysOnTop) context.pose().translate(0, 0, 500);
        super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
        if (this.alwaysOnTop) context.pose().translate(0, 0, -500);
    }

    @Override
    public boolean canFocus(UIComponent.FocusSource source) {
        return source == UIComponent.FocusSource.MOUSE_CLICK;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        this.xOffset += deltaX;
        this.yOffset += deltaY;

        super.x((int) (this.baseX + Math.round(this.xOffset)));
        super.y((int) (this.baseY + Math.round(this.yOffset)));
        return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public @Nullable UIComponent childAt(int x, int y) {
        if (this.isInBoundingBox(x, y) && y - this.y < this.foreheadSize) {
            return this;
        }

        return super.childAt(x, y);
    }

    @Override
    public DraggableContainer<C> x(int x) {
        this.baseX = x;
        super.x((int) (x + Math.round(this.xOffset)));
        return this;
    }

    @Override
    public DraggableContainer<C> y(int y) {
        this.baseY = y;
        super.y((int) (y + Math.round(this.yOffset)));
        return this;
    }

    @Override
    public int baseX() {
        return this.baseX;
    }

    @Override
    public int baseY() {
        return this.baseY;
    }

    @Override
    public ParentUIComponent padding(Insets padding) {
        return super.padding(
                Insets.of(padding.top() + this.foreheadSize, padding.bottom(), padding.left(), padding.right()));
    }

    public DraggableContainer<C> foreheadSize(int foreheadSize) {
        int prevForeheadSize = this.foreheadSize;
        this.foreheadSize = foreheadSize;

        var padding = this.padding.get();
        this.padding(Insets.of(padding.top() - prevForeheadSize, padding.bottom(), padding.left(), padding.right()));
        return this;
    }

    public int foreheadSize() {
        return this.foreheadSize;
    }

    public DraggableContainer<C> alwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
        return this;
    }

    public boolean alwaysOnTop() {
        return this.alwaysOnTop;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "forehead-size", UIParsing::parseUnsignedInt, this::foreheadSize);
        UIParsing.apply(children, "always-on-top", UIParsing::parseBool, this::alwaysOnTop);
    }
}
