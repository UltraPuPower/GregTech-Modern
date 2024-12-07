package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.component.VanillaWidgetComponent;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.event.*;
import com.gregtechceu.gtceu.api.ui.inject.UIComponentStub;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.EventSource;
import com.gregtechceu.gtceu.api.ui.util.FocusHandler;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements UIComponentStub, GuiEventListener {
    @Shadow public boolean active;

    @Unique
    protected VanillaWidgetComponent ui$wrapper = null;

    @Override
    public void inflate(Size space) {
        this.ui$getWrapper().inflate(space);
    }

    @Override
    public void mount(ParentUIComponent parent, int x, int y) {
        this.ui$getWrapper().mount(parent, x, y);
    }

    @Override
    public void dismount(DismountReason reason) {
        this.ui$getWrapper().dismount(reason);
    }

    @Nullable
    @Override
    public ParentUIComponent parent() {
        return this.ui$getWrapper().parent();
    }

    @Override
    public @Nullable FocusHandler focusHandler() {
        return this.ui$getWrapper().focusHandler();
    }

    @Override
    public UIComponent positioning(Positioning positioning) {
        this.ui$getWrapper().positioning(positioning);
        return this;
    }

    @Override
    public AnimatableProperty<Positioning> positioning() {
        return this.ui$getWrapper().positioning();
    }

    @Override
    public UIComponent margins(Insets margins) {
        this.ui$getWrapper().margins(margins);
        return this;
    }

    @Override
    public AnimatableProperty<Insets> margins() {
        return this.ui$getWrapper().margins();
    }

    @Override
    public UIComponent horizontalSizing(Sizing horizontalSizing) {
        this.ui$getWrapper().horizontalSizing(horizontalSizing);
        return this;
    }

    @Override
    public UIComponent verticalSizing(Sizing verticalSizing) {
        this.ui$getWrapper().verticalSizing(verticalSizing);
        return this;
    }

    @Override
    public AnimatableProperty<Sizing> horizontalSizing() {
        return this.ui$getWrapper().horizontalSizing();
    }

    @Override
    public AnimatableProperty<Sizing> verticalSizing() {
        return this.ui$getWrapper().verticalSizing();
    }

    @Override
    public EventSource<MouseDown> mouseDown() {
        return this.ui$getWrapper().mouseDown();
    }

    @Override
    public int x() {
        return this.ui$getWrapper().x();
    }

    @Override
    public int y() {
        return this.ui$getWrapper().y();
    }

    @Override
    public int width() {
        return this.ui$getWrapper().width();
    }

    @Override
    public int height() {
        return this.ui$getWrapper().height();
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        this.ui$getWrapper().draw(graphics, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        return this.ui$getWrapper().shouldDrawTooltip(mouseX, mouseY);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        this.ui$getWrapper().update(delta, mouseX, mouseY);
        this.cursorStyle(this.active ? this.ui$preferredCursorStyle() : CursorStyle.POINTER);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.ui$getWrapper().onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return this.ui$getWrapper().onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public EventSource<MouseUp> mouseUp() {
        return this.ui$getWrapper().mouseUp();
    }

    @Override
    public EventSource<MouseScroll> mouseScroll() {
        return this.ui$getWrapper().mouseScroll();
    }

    @Override
    public EventSource<MouseDrag> mouseDrag() {
        return this.ui$getWrapper().mouseDrag();
    }

    @Override
    public EventSource<KeyPress> keyPress() {
        return this.ui$getWrapper().keyPress();
    }

    @Override
    public EventSource<CharTyped> charTyped() {
        return this.ui$getWrapper().charTyped();
    }

    @Override
    public EventSource<FocusGained> focusGained() {
        return this.ui$getWrapper().focusGained();
    }

    @Override
    public EventSource<FocusLost> focusLost() {
        return this.ui$getWrapper().focusLost();
    }

    @Override
    public EventSource<MouseEnter> mouseEnter() {
        return this.ui$getWrapper().mouseEnter();
    }

    @Override
    public EventSource<MouseLeave> mouseLeave() {
        return this.ui$getWrapper().mouseLeave();
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.ui$getWrapper().onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        return this.ui$getWrapper().onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.ui$getWrapper().onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.ui$getWrapper().onCharTyped(chr, modifiers);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    @Override
    public void onFocusGained(FocusSource source) {
        this.setFocused(source == FocusSource.KEYBOARD_CYCLE);
        this.ui$getWrapper().onFocusGained(source);
    }

    @Override
    public void onFocusLost() {
        this.setFocused(false);
        this.ui$getWrapper().onFocusLost();
    }

    @Override
    public <C extends UIComponent> C configure(Consumer<C> closure) {
        return this.ui$getWrapper().configure(closure);
    }

    @Override
    public void parseProperties(UIModel spec, Element element, Map<String, Element> children) {
        // --- copied from Component, because you can't invoke interface super methods in mixins - very cool ---

        if (!element.getAttribute("id").isBlank()) {
            this.id(element.getAttribute("id").strip());
        }

        UIParsing.apply(children, "margins", Insets::parse, this::margins);
        UIParsing.apply(children, "positioning", Positioning::parse, this::positioning);
        UIParsing.apply(children, "z-index", UIParsing::parseSignedInt, this::zIndex);
        UIParsing.apply(children, "cursor-style", UIParsing.parseEnum(CursorStyle.class), this::cursorStyle);
        UIParsing.apply(children, "tooltip-text", UIParsing::parseText, this::tooltip);

        if (children.containsKey("sizing")) {
            var sizingValues = UIParsing.childElements(children.get("sizing"));
            UIParsing.apply(sizingValues, "vertical", Sizing::parse, this::verticalSizing);
            UIParsing.apply(sizingValues, "horizontal", Sizing::parse, this::horizontalSizing);
        }

        // --- end ---

        UIParsing.apply(children, "active", UIParsing::parseBool, active -> this.active = active);
    }

    @Override
    public CursorStyle cursorStyle() {
        return this.ui$getWrapper().cursorStyle();
    }

    @Override
    public UIComponent cursorStyle(CursorStyle style) {
        return this.ui$getWrapper().cursorStyle(style);
    }

    @Override
    public UIComponent tooltip(List<ClientTooltipComponent> tooltip) {
        return this.ui$getWrapper().tooltip(tooltip);
    }

    @Override
    public List<ClientTooltipComponent> tooltip() {
        return this.ui$getWrapper().tooltip();
    }

    @Override
    public UIComponent zIndex(int zIndex) {
        return this.ui$getWrapper().zIndex(zIndex);
    }

    @Override
    public int zIndex() {
        return this.ui$getWrapper().zIndex();
    }

    @Override
    public UIComponent id(@Nullable String id) {
        this.ui$getWrapper().id(id);
        return this;
    }

    @Override
    public @Nullable String id() {
        return this.ui$getWrapper().id();
    }

    @Unique
    protected VanillaWidgetComponent ui$getWrapper() {
        if (this.ui$wrapper == null) {
            this.ui$wrapper = UIComponents.wrapVanillaWidget((AbstractWidget) (Object) this);
        }

        return this.ui$wrapper;
    }

    @Override
    public @Nullable VanillaWidgetComponent widgetWrapper() {
        return this.ui$wrapper;
    }

    @Override
    public int xOffset() {
        return 0;
    }

    @Override
    public int yOffset() {
        return 0;
    }

    @Override
    public int widthOffset() {
        return 0;
    }

    @Override
    public int heightOffset() {
        return 0;
    }

    @Inject(method = "setWidth", at = @At("HEAD"), cancellable = true)
    private void applyWidthToWrapper(int width, CallbackInfo ci) {
        var wrapper = this.ui$wrapper;
        if (wrapper != null) {
            wrapper.horizontalSizing(Sizing.fixed(width));
            ci.cancel();
        }
    }

    @Override
    public void updateX(int x) {
        this.ui$getWrapper().updateX(x);
    }

    @Override
    public void updateY(int y) {
        this.ui$getWrapper().updateY(y);
    }

    protected CursorStyle ui$preferredCursorStyle() {
        return CursorStyle.POINTER;
    }
}
