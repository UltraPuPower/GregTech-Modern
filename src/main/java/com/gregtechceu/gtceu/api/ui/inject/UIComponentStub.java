package com.gregtechceu.gtceu.api.ui.inject;

import com.gregtechceu.gtceu.api.ui.component.VanillaWidgetComponent;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.event.*;
import com.gregtechceu.gtceu.api.ui.util.EventSource;
import com.gregtechceu.gtceu.api.ui.util.FocusHandler;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface UIComponentStub extends UIComponent {

    @Override
    default void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default @Nullable ParentUIComponent parent() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponentMenuAccess containerAccess() {
        throw new NotImplementedException("Interface stub method called");
    }

    @ApiStatus.Internal
    @Override
    default void containerAccess(UIComponentMenuAccess access) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default @Nullable FocusHandler focusHandler() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent positioning(Positioning positioning) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default AnimatableProperty<Positioning> positioning() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent margins(Insets margins) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default AnimatableProperty<Insets> margins() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent horizontalSizing(Sizing horizontalSizing) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent verticalSizing(Sizing verticalSizing) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default AnimatableProperty<Sizing> horizontalSizing() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default AnimatableProperty<Sizing> verticalSizing() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<MouseEnter> mouseEnter() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<MouseLeave> mouseLeave() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default CursorStyle cursorStyle() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent cursorStyle(CursorStyle style) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent tooltip(@NotNull BiConsumer<UIComponent, List<Component>> tooltip) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent tooltip(List<ClientTooltipComponent> tooltip) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default List<ClientTooltipComponent> tooltip() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent zIndex(int zIndex) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default int zIndex() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent inflate(Size space) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default boolean enabled() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent enabled(boolean enabled) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default void applySizing() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default void mount(ParentUIComponent parent, int x, int y) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<Mount> mount() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default void dismount(DismountReason reason) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<Dismount> dismount() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default <C extends UIComponent> C configure(Consumer<C> closure) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default int width() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default int height() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default boolean onMouseDown(double mouseX, double mouseY, int button) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<MouseDown> mouseDown() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default boolean onMouseUp(double mouseX, double mouseY, int button) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<MouseUp> mouseUp() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<MouseScroll> mouseScroll() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default boolean onMouseMoved(double mouseX, double mouseY) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<MouseMoved> mouseMoved() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<MouseDrag> mouseDrag() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<KeyPress> keyPress() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default boolean onCharTyped(char chr, int modifiers) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<CharTyped> charTyped() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default void onFocusGained(FocusSource source, UIComponent lastFocus) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<FocusGained> focusGained() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default void onFocusLost() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default EventSource<FocusLost> focusLost() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default int x() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent x(int x) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default int y() {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent y(int y) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default UIComponent id(@Nullable String id) {
        throw new NotImplementedException("Interface stub method called");
    }

    @Override
    default @Nullable String id() {
        throw new NotImplementedException("Interface stub method called");
    }

    default VanillaWidgetComponent widgetWrapper() {
        throw new NotImplementedException("Interface stub method called");
    }

    default int xOffset() {
        throw new NotImplementedException("Interface stub method called");
    }

    default int yOffset() {
        throw new NotImplementedException("Interface stub method called");
    }

    default int widthOffset() {
        throw new NotImplementedException("Interface stub method called");
    }

    default int heightOffset() {
        throw new NotImplementedException("Interface stub method called");
    }
}
