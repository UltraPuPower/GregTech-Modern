package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.component.UIComponents;
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

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements UIComponentStub, GuiEventListener {

    @Shadow
    public boolean active;

    @Unique
    protected VanillaWidgetComponent gtceu$wrapper = null;

    @Override
    public VanillaWidgetComponent inflate(Size space) {
        this.gtceu$getWrapper().inflate(space);
        return this.gtceu$getWrapper();
    }

    @Override
    public void applySizing() {
        this.gtceu$getWrapper().applySizing();
    }

    @Override
    public void mount(ParentUIComponent parent, int x, int y) {
        this.gtceu$getWrapper().mount(parent, x, y);
    }

    @Override
    public void dismount(DismountReason reason) {
        this.gtceu$getWrapper().dismount(reason);
    }

    @Nullable
    @Override
    public ParentUIComponent parent() {
        return this.gtceu$getWrapper().parent();
    }

    @Override
    public UIComponentMenuAccess containerAccess() {
        return this.gtceu$getWrapper().containerAccess();
    }

    @ApiStatus.Internal
    @Override
    public void containerAccess(UIComponentMenuAccess access) {
        this.gtceu$getWrapper().containerAccess(access);
    }

    @Override
    public @Nullable FocusHandler focusHandler() {
        return this.gtceu$getWrapper().focusHandler();
    }

    @Override
    public UIComponent positioning(Positioning positioning) {
        this.gtceu$getWrapper().positioning(positioning);
        return this;
    }

    @Override
    public AnimatableProperty<Positioning> positioning() {
        return this.gtceu$getWrapper().positioning();
    }

    @Override
    public UIComponent margins(Insets margins) {
        this.gtceu$getWrapper().margins(margins);
        return this;
    }

    @Override
    public AnimatableProperty<Insets> margins() {
        return this.gtceu$getWrapper().margins();
    }

    @Override
    public UIComponent horizontalSizing(Sizing horizontalSizing) {
        this.gtceu$getWrapper().horizontalSizing(horizontalSizing);
        return this;
    }

    @Override
    public UIComponent verticalSizing(Sizing verticalSizing) {
        this.gtceu$getWrapper().verticalSizing(verticalSizing);
        return this;
    }

    @Override
    public AnimatableProperty<Sizing> horizontalSizing() {
        return this.gtceu$getWrapper().horizontalSizing();
    }

    @Override
    public AnimatableProperty<Sizing> verticalSizing() {
        return this.gtceu$getWrapper().verticalSizing();
    }

    @Override
    public EventSource<MouseDown> mouseDown() {
        return this.gtceu$getWrapper().mouseDown();
    }

    @Override
    public int x() {
        return this.gtceu$getWrapper().x();
    }

    @Override
    public int y() {
        return this.gtceu$getWrapper().y();
    }

    @Override
    public int width() {
        return this.gtceu$getWrapper().width();
    }

    @Override
    public int height() {
        return this.gtceu$getWrapper().height();
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        this.gtceu$getWrapper().draw(graphics, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        return this.gtceu$getWrapper().shouldDrawTooltip(mouseX, mouseY);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        this.gtceu$getWrapper().update(delta, mouseX, mouseY);
        this.cursorStyle(this.active ? this.gtceu$preferredCursorStyle() : CursorStyle.POINTER);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.gtceu$getWrapper().onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return this.gtceu$getWrapper().onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public EventSource<MouseUp> mouseUp() {
        return this.gtceu$getWrapper().mouseUp();
    }

    @Override
    public EventSource<MouseScroll> mouseScroll() {
        return this.gtceu$getWrapper().mouseScroll();
    }

    @Override
    public EventSource<MouseDrag> mouseDrag() {
        return this.gtceu$getWrapper().mouseDrag();
    }

    @Override
    public EventSource<MouseMoved> mouseMoved() {
        return this.gtceu$getWrapper().mouseMoved();
    }

    @Override
    public EventSource<KeyPress> keyPress() {
        return this.gtceu$getWrapper().keyPress();
    }

    @Override
    public EventSource<CharTyped> charTyped() {
        return this.gtceu$getWrapper().charTyped();
    }

    @Override
    public EventSource<FocusGained> focusGained() {
        return this.gtceu$getWrapper().focusGained();
    }

    @Override
    public EventSource<FocusLost> focusLost() {
        return this.gtceu$getWrapper().focusLost();
    }

    @Override
    public EventSource<MouseEnter> mouseEnter() {
        return this.gtceu$getWrapper().mouseEnter();
    }

    @Override
    public EventSource<MouseLeave> mouseLeave() {
        return this.gtceu$getWrapper().mouseLeave();
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.gtceu$getWrapper().onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        return this.gtceu$getWrapper().onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public boolean onMouseMoved(double mouseX, double mouseY) {
        return this.gtceu$getWrapper().onMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.gtceu$getWrapper().onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.gtceu$getWrapper().onCharTyped(chr, modifiers);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    @Override
    public void onFocusGained(FocusSource source, UIComponent lastFocus) {
        this.setFocused(source == FocusSource.KEYBOARD_CYCLE);
        this.gtceu$getWrapper().onFocusGained(source, lastFocus);
    }

    @Override
    public void onFocusLost() {
        this.setFocused(false);
        this.gtceu$getWrapper().onFocusLost();
    }

    @Override
    public <C extends UIComponent> C configure(Consumer<C> closure) {
        return this.gtceu$getWrapper().configure(closure);
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
        UIParsing.apply(children, "tooltip-text", UIParsing::parseComponent, this::tooltip);

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
        return this.gtceu$getWrapper().cursorStyle();
    }

    @Override
    public UIComponent cursorStyle(CursorStyle style) {
        return this.gtceu$getWrapper().cursorStyle(style);
    }

    @Override
    public UIComponent tooltip(@NotNull BiConsumer<UIComponent, List<Component>> tooltip) {
        return this.gtceu$getWrapper().tooltip(tooltip);
    }

    @Override
    public UIComponent tooltip(List<ClientTooltipComponent> tooltip) {
        return this.gtceu$getWrapper().tooltip(tooltip);
    }

    @Override
    public List<ClientTooltipComponent> tooltip() {
        return this.gtceu$getWrapper().tooltip();
    }

    @Override
    public UIComponent zIndex(int zIndex) {
        return this.gtceu$getWrapper().zIndex(zIndex);
    }

    @Override
    public int zIndex() {
        return this.gtceu$getWrapper().zIndex();
    }

    @Override
    public UIComponent id(@Nullable String id) {
        this.gtceu$getWrapper().id(id);
        return this;
    }

    @Override
    public @Nullable String id() {
        return this.gtceu$getWrapper().id();
    }

    @Unique
    protected VanillaWidgetComponent gtceu$getWrapper() {
        if (this.gtceu$wrapper == null) {
            this.gtceu$wrapper = UIComponents.wrapVanillaWidget((AbstractWidget) (Object) this);
        }

        return this.gtceu$wrapper;
    }

    @Override
    public @Nullable VanillaWidgetComponent widgetWrapper() {
        return this.gtceu$wrapper;
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
        var wrapper = this.gtceu$wrapper;
        if (wrapper != null) {
            wrapper.horizontalSizing(Sizing.fixed(width));
            ci.cancel();
        }
    }

    @Override
    public UIComponent x(int x) {
        this.gtceu$getWrapper().x(x);
        return this.gtceu$getWrapper();
    }

    @Override
    public UIComponent y(int y) {
        this.gtceu$getWrapper().y(y);
        return this.gtceu$getWrapper();
    }

    protected CursorStyle gtceu$preferredCursorStyle() {
        return CursorStyle.POINTER;
    }
}
