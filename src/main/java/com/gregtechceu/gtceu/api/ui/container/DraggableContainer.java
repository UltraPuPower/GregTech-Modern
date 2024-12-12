package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.Map;

@Accessors(fluent = true, chain = true)
public class DraggableContainer<C extends UIComponent> extends WrappingParentUIComponent<C> {

    @Getter
    protected int foreheadSize = 10;

    @Getter
    protected int baseX = 0, baseY = 0;
    protected double xOffset = 0, yOffset = 0;

    protected DraggableContainer(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        super(horizontalSizing, verticalSizing, child);
        this.padding(Insets.none());
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        this.drawChildren(graphics, mouseX, mouseY, partialTicks, delta, this.childView);
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

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "forehead-size", UIParsing::parseUnsignedInt, this::foreheadSize);
    }
}
