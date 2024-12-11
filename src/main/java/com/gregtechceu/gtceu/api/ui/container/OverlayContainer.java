package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.core.*;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;

public class OverlayContainer<C extends UIComponent> extends WrappingParentUIComponent<C> {

    /**
     * Whether this overlay should close when a mouse
     * click occurs outside the bounds of its contents
     */
    @Getter
    @Setter
    protected boolean closeOnClick = true;

    protected OverlayContainer(C child) {
        super(Sizing.fill(100), Sizing.fill(100), child);

        this.positioning(Positioning.absolute(0, 0));
        this.surface(Surface.VANILLA_TRANSLUCENT);
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        this.drawChildren(graphics, mouseX, mouseY, partialTicks, delta, this.childView);
    }

    @Override
    public void drawFocusHighlight(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {}

    @Override
    public void mount(ParentUIComponent parent, int x, int y) {
        super.mount(parent, x, y);
        this.parent.focusHandler().focus(this, FocusSource.KEYBOARD_CYCLE);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        boolean handled = super.onMouseDown(mouseX, mouseY, button);

        if (!handled && this.closeOnClick) {
            this.remove();
            return true;
        } else {
            return handled;
        }
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        boolean handled = super.onKeyPress(keyCode, scanCode, modifiers);

        // TODO properly receive this event in the first place
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.remove();
            return true;
        }

        return handled;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.KEYBOARD_CYCLE;
    }

    @Override
    protected int childMountX() {
        return this.padding.get().left() + (this.width - this.child.fullSize().width()) / 2;
    }

    @Override
    protected int childMountY() {
        return this.padding.get().top() + (this.height() - this.child.fullSize().height()) / 2;
    }
}
