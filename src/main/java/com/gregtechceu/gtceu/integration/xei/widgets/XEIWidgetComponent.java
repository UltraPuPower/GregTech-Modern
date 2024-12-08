package com.gregtechceu.gtceu.integration.xei.widgets;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;

import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;

public class XEIWidgetComponent extends BaseUIComponent {

    private final WidgetWithBounds widget;

    public XEIWidgetComponent(WidgetWithBounds widget) {
        this.widget = widget;

        var bounds = widget.getBounds();
        this.horizontalSizing.set(Sizing.fixed(bounds.getWidth()));
        this.verticalSizing.set(Sizing.fixed(bounds.getHeight()));

        this.mouseEnter().subscribe(() -> {
            this.focusHandler().focus(this, FocusSource.KEYBOARD_CYCLE);
        });

        this.mouseLeave().subscribe(() -> {
            this.focusHandler().focus(null, null);
        });
    }

    @Override
    public void mount(ParentUIComponent parent, int x, int y) {
        super.mount(parent, x, y);
        this.applyToWidget();
    }

    @Override
    public void draw(UIGuiGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        this.widget.render(context, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawFocusHighlight(UIGuiGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {}

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.widget.getBounds().getWidth();
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.widget.getBounds().getHeight();
    }

    @Override
    public XEIWidgetComponent x(int x) {
        super.x(x);
        this.applyToWidget();
        return this;
    }

    @Override
    public XEIWidgetComponent y(int y) {
        super.y(y);
        this.applyToWidget();
        return this;
    }

    private void applyToWidget() {
        var bounds = this.widget.getBounds();

        bounds.x = this.x;
        bounds.y = this.y;

        bounds.width = this.width;
        bounds.height = this.height;
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.widget.mouseClicked(this.x + mouseX, this.y + mouseY, button) |
                super.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return this.widget.mouseReleased(this.x + mouseX, this.y + mouseY, button) |
                super.onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.widget.mouseScrolled(this.x + mouseX, this.y + mouseY, amount) |
                super.onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        return this.widget.mouseDragged(this.x + mouseX, this.y + mouseY, button, deltaX, deltaY) |
                super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.widget.charTyped(chr, modifiers) | super.onCharTyped(chr, modifiers);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.widget.keyPressed(keyCode, scanCode, modifiers) | super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }
}
