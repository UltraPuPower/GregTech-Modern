package com.gregtechceu.gtceu.integration.emi;

import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.util.ScissorStack;
import com.gregtechceu.gtceu.integration.xei.widgets.XEIWidgetComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class EMIUIAdapter<T extends ParentUIComponent> extends Widget implements ContainerEventHandler {

    public static final ScreenPosition LAYOUT = new ScreenPosition(-69, -69);

    public final UIAdapter<T> adapter;

    @Getter
    private final Bounds bounds;
    @Getter
    @Setter
    private boolean isDragging;
    @Getter
    @Setter
    private GuiEventListener focused;

    public EMIUIAdapter(Bounds bounds, BiFunction<Sizing, Sizing, T> rootComponentMaker) {
        this.adapter = UIAdapter.createWithoutScreen(bounds.x(), bounds.y(), bounds.width(), bounds.height(),
                rootComponentMaker);
        this.adapter.inspectorZOffset = 900;
        this.bounds = bounds;

        if (Minecraft.getInstance().screen != null) {
            MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Closing event) -> this.adapter.dispose());
        }
    }

    public void prepare() {
        this.adapter.inflateAndMount();
    }

    public T rootComponent() {
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
    public List<? extends GuiEventListener> children() {
        return List.of();
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
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        ScissorStack.push(this.adapter.x(), this.adapter.y(), this.adapter.width(), this.adapter.height(),
                context.pose());
        this.adapter.render(context, mouseX, mouseY, partialTick);
        ScissorStack.pop();

        context.flush();
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
    @Tolerate
    public void setFocused(boolean focused) {
        adapter.setFocused(focused);
    }

    @Override
    public boolean isFocused() {
        return adapter.isFocused();
    }
}
