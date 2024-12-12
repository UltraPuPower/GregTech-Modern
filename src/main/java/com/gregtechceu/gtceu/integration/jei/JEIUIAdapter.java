package com.gregtechceu.gtceu.integration.jei;

import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.util.ScissorStack;
import com.gregtechceu.gtceu.integration.xei.widgets.XEIWidgetComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.Rect2i;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import lombok.Getter;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import mezz.jei.api.gui.widgets.IRecipeWidget;

import java.util.function.Consumer;
import java.util.function.Function;

public class JEIUIAdapter implements IRecipeWidget, GuiEventListener {

    public static final ScreenPosition LAYOUT = new ScreenPosition(-69, -69);

    public final UIAdapter<UIComponentGroup> adapter;

    @Getter
    private final ScreenPosition position;
    @Getter
    private final ScreenRectangle area;

    public JEIUIAdapter(Rect2i bounds) {
        this.adapter = UIAdapter.createWithoutScreen(bounds.getX(), bounds.getY(), bounds.getWidth(),
                bounds.getHeight(), UIContainers::group);
        this.adapter.inspectorZOffset = 900;
        this.position = new ScreenPosition(bounds.getX(), bounds.getY());
        this.area = new ScreenRectangle(position, bounds.getWidth(), bounds.getHeight());

        if (Minecraft.getInstance().screen != null) {
            MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Closing event) -> this.adapter.dispose());
        }
    }

    public void prepare() {
        this.adapter.inflateAndMount();
    }

    public UIComponentGroup rootComponent() {
        return this.adapter.rootComponent;
    }

    public <W extends WidgetWithBounds> XEIWidgetComponent wrap(W widget) {
        return new XEIWidgetComponent(widget);
    }

    public <W extends WidgetWithBounds> XEIWidgetComponent wrap(Function<ScreenPosition, W> widgetFactory,
                                                                Consumer<W> widgetConfigurator) {
        var widget = widgetFactory.apply(LAYOUT);
        widgetConfigurator.accept(widget);
        return new XEIWidgetComponent(widget);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.adapter.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.adapter.mouseClicked(mouseX - this.adapter.x(), mouseY - this.adapter.y(), button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.adapter.mouseScrolled(mouseX - this.adapter.x(), mouseY - this.adapter.y(), amount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.adapter.mouseReleased(mouseX - this.adapter.x(), mouseY - this.adapter.y(), button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.adapter.mouseDragged(mouseX - this.adapter.x(), mouseY - this.adapter.y(), button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.adapter.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.adapter.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.adapter.charTyped(chr, modifiers);
    }

    @Override
    public void setFocused(boolean focused) {
        adapter.setFocused(focused);
    }

    @Override
    public boolean isFocused() {
        return adapter.isFocused();
    }

    @Override
    public void drawWidget(GuiGraphics context, double mouseX, double mouseY) {
        ScissorStack.push(this.adapter.x(), this.adapter.y(), this.adapter.width(), this.adapter.height(),
                context.pose());
        this.adapter.render(context, (int) mouseX, (int) mouseY, Minecraft.getInstance().getPartialTick());
        ScissorStack.pop();

        context.flush();
    }
}
