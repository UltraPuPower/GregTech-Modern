package com.gregtechceu.gtceu.api.ui.base;

import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.event.*;
import com.gregtechceu.gtceu.api.ui.util.EventSource;
import com.gregtechceu.gtceu.api.ui.util.EventStream;
import com.gregtechceu.gtceu.api.ui.util.FocusHandler;
import com.gregtechceu.gtceu.api.ui.util.Observable;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@Accessors(fluent = true, chain = true)
public abstract class BaseUIComponent implements UIComponent {

    @Nullable
    @Getter
    protected ParentUIComponent parent = null;
    @Nullable
    protected UIComponentMenuAccess access = null;
    @Nullable
    @Getter
    @Setter
    protected String id = null;
    @Getter
    @Setter
    protected int zIndex = 0;

    protected boolean mounted = false;

    protected int batchedEvents = 0;

    @Getter
    protected final AnimatableProperty<Insets> margins = AnimatableProperty.of(Insets.none());

    @Getter
    protected final AnimatableProperty<Positioning> positioning = AnimatableProperty.of(Positioning.layout());
    @Getter
    protected final AnimatableProperty<Sizing> horizontalSizing = AnimatableProperty.of(Sizing.content());
    @Getter
    protected final AnimatableProperty<Sizing> verticalSizing = AnimatableProperty.of(Sizing.content());

    protected final EventStream<MouseDown> mouseDownEvents = MouseDown.newStream();
    protected final EventStream<MouseUp> mouseUpEvents = MouseUp.newStream();
    protected final EventStream<MouseScroll> mouseScrollEvents = MouseScroll.newStream();
    protected final EventStream<MouseDrag> mouseDragEvents = MouseDrag.newStream();
    protected final EventStream<KeyPress> keyPressEvents = KeyPress.newStream();
    protected final EventStream<CharTyped> charTypedEvents = CharTyped.newStream();
    protected final EventStream<FocusGained> focusGainedEvents = FocusGained.newStream();
    protected final EventStream<FocusLost> focusLostEvents = FocusLost.newStream();

    protected final EventStream<MouseEnter> mouseEnterEvents = MouseEnter.newStream();
    protected final EventStream<MouseLeave> mouseLeaveEvents = MouseLeave.newStream();

    protected boolean hovered = false;
    protected boolean dirty = false;

    protected CursorStyle cursorStyle = CursorStyle.NONE;
    protected List<ClientTooltipComponent> tooltip = List.of();

    @Getter
    @Setter
    protected int x, y;
    @Getter
    @Setter
    protected int width, height;

    protected Size space = Size.zero();

    protected BaseUIComponent() {
        Observable.observeAll(this::notifyParentIfMounted, margins, positioning, horizontalSizing, verticalSizing);
    }

    /**
     * @return The horizontal size this component needs to fit its contents
     */
    protected int determineHorizontalContentSize(Sizing sizing) {
        throw new UnsupportedOperationException(
                this.getClass().getSimpleName() + " does not support Sizing.content() on the horizontal axis");
    }

    /**
     * @return The vertical size this component needs to fit its contents
     */
    protected int determineVerticalContentSize(Sizing sizing) {
        throw new UnsupportedOperationException(
                this.getClass().getSimpleName() + " does not support Sizing.content() on the vertical axis");
    }

    @Override
    public BaseUIComponent inflate(Size space) {
        this.space = space;
        this.applySizing();
        this.dirty = false;
        return this;
    }

    /**
     * Calculate and apply the sizing of this component
     * according to the last known expansion space
     */
    public void applySizing() {
        final var horizontalSizing = this.horizontalSizing.get();
        final var verticalSizing = this.verticalSizing.get();

        final var margins = this.margins.get();

        this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(),
                this::determineHorizontalContentSize);
        this.height = verticalSizing.inflate(this.space.height() - margins.vertical(),
                this::determineVerticalContentSize);
    }

    protected void notifyParentIfMounted() {
        if (!this.hasParent()) return;

        if (this.batchedEvents > 0) {
            this.batchedEvents++;
            return;
        }

        this.dirty = true;
        this.parent.onChildMutated(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends UIComponent> C configure(Consumer<C> closure) {
        try {
            this.runAndDeferEvents(() -> closure.accept((C) this));
        } catch (ClassCastException theUserDidBadItWasNotMyFault) {
            throw new IllegalArgumentException(
                    "Invalid target class passed when configuring component of type " + this.getClass().getSimpleName(),
                    theUserDidBadItWasNotMyFault);
        }

        return (C) this;
    }

    protected void runAndDeferEvents(Runnable action) {
        try {
            this.batchedEvents = 1;
            action.run();
        } finally {
            if (this.batchedEvents > 1) {
                this.batchedEvents = 0;
                this.notifyParentIfMounted();
            } else {
                this.batchedEvents = 0;
            }
        }
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        UIComponent.super.update(delta, mouseX, mouseY);

        boolean nowHovered = this.isInBoundingBox(mouseX, mouseY);
        if (this.hovered != nowHovered) {
            this.hovered = nowHovered;

            if (nowHovered) {
                this.mouseEnterEvents.sink().onMouseEnter();
            } else {
                this.mouseLeaveEvents.sink().onMouseLeave();
            }
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.mouseDownEvents.sink().onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public EventSource<MouseDown> mouseDown() {
        return this.mouseDownEvents.source();
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return this.mouseUpEvents.sink().onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public EventSource<MouseUp> mouseUp() {
        return this.mouseUpEvents.source();
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.mouseScrollEvents.sink().onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public EventSource<MouseScroll> mouseScroll() {
        return this.mouseScrollEvents.source();
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        return this.mouseDragEvents.sink().onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public EventSource<MouseDrag> mouseDrag() {
        return this.mouseDragEvents.source();
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.keyPressEvents.sink().onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public EventSource<KeyPress> keyPress() {
        return this.keyPressEvents.source();
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.charTypedEvents.sink().onCharTyped(chr, modifiers);
    }

    @Override
    public EventSource<CharTyped> charTyped() {
        return this.charTypedEvents.source();
    }

    @Override
    public void onFocusGained(FocusSource source) {
        this.focusGainedEvents.sink().onFocusGained(source);
    }

    @Override
    public EventSource<FocusGained> focusGained() {
        return this.focusGainedEvents.source();
    }

    @Override
    public void onFocusLost() {
        this.focusLostEvents.sink().onFocusLost();
    }

    @Override
    public EventSource<FocusLost> focusLost() {
        return this.focusLostEvents.source();
    }

    @Override
    public EventSource<MouseEnter> mouseEnter() {
        return this.mouseEnterEvents.source();
    }

    @Override
    public EventSource<MouseLeave> mouseLeave() {
        return this.mouseLeaveEvents.source();
    }

    @Override
    public CursorStyle cursorStyle() {
        return this.cursorStyle;
    }

    @Override
    public BaseUIComponent cursorStyle(CursorStyle style) {
        this.cursorStyle = style;
        return this;
    }

    @Override
    public UIComponent tooltip(List<ClientTooltipComponent> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public List<ClientTooltipComponent> tooltip() {
        return this.tooltip;
    }

    @Override
    public void mount(ParentUIComponent parent, int x, int y) {
        this.parent = parent;
        this.mounted = true;
        this.moveTo(x, y);
    }

    @Override
    public void dismount(DismountReason reason) {
        this.parent = null;
        this.mounted = false;
    }

    @Override
    public UIComponentMenuAccess containerAccess() {
        return this.access != null ? this.access :
                this.parent() != null ? this.parent().containerAccess() : null;
    }

    @ApiStatus.Internal
    @Override
    public void setContainerAccess(UIComponentMenuAccess access) {
        this.access = access;
    }

    @Override
    public @Nullable FocusHandler focusHandler() {
        return this.hasParent() ? this.parent.focusHandler() : null;
    }

    @Nullable
    public Player player() {
        if(containerAccess() == null || containerAccess().menu() == null) return null;
        return containerAccess().menu().player();
    }

    public ItemStack getCarried() {
        if(containerAccess() == null || containerAccess().menu() == null) return ItemStack.EMPTY;
        return containerAccess().menu().getCarried();
    }

    public void setCarried(@NotNull ItemStack stack) {
        if(containerAccess() == null || containerAccess().menu() == null) return;
        containerAccess().menu().setCarried(stack);
    }

    @Override
    public BaseUIComponent positioning(Positioning positioning) {
        this.positioning.set(positioning);
        return this;
    }

    @Override
    public BaseUIComponent margins(Insets margins) {
        this.margins.set(margins);
        return this;
    }

    @Override
    public UIComponent horizontalSizing(Sizing horizontalSizing) {
        this.horizontalSizing.set(horizontalSizing);
        return this;
    }

    @Override
    public UIComponent verticalSizing(Sizing verticalSizing) {
        this.verticalSizing.set(verticalSizing);
        return this;
    }
}
