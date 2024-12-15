package com.gregtechceu.gtceu.integration.jei.handler;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import lombok.Getter;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class UIEventListener<T extends UIComponent> implements IJeiGuiEventListener {

    private final UIComponent component;
    @Getter
    private final ScreenRectangle area;

    public UIEventListener(UIComponent component) {
        this.component = component;
        this.area = new ScreenRectangle(component.baseX(), component.baseY(), component.width(), component.height());
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        component.onMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return component.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return component.onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return component.onMouseDrag(mouseX, mouseY, dragX, dragY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        return component.onMouseScroll(mouseX, mouseY, scrollDelta);
    }

    @Override
    public boolean keyPressed(double mouseX, double mouseY, int keyCode, int scanCode, int modifiers) {
        return component.onKeyPress(keyCode, scanCode, modifiers);
    }
}
