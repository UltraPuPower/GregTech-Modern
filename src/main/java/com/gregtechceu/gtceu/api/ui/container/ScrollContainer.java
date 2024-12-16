package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelParsingException;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.texture.NinePatchTexture;
import com.gregtechceu.gtceu.api.ui.util.Delta;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Accessors(fluent = true, chain = true)
public class ScrollContainer<C extends UIComponent> extends WrappingParentUIComponent<C> {

    public static final ResourceLocation VERTICAL_VANILLA_SCROLLBAR_TEXTURE = new ResourceLocation("owo",
            "scrollbar/vanilla_vertical");
    public static final ResourceLocation DISABLED_VERTICAL_VANILLA_SCROLLBAR_TEXTURE = new ResourceLocation("owo",
            "scrollbar/vanilla_vertical_disabled");
    public static final ResourceLocation HORIZONTAL_VANILLA_SCROLLBAR_TEXTURE = new ResourceLocation("owo",
            "scrollbar/vanilla_horizontal_disabled");
    public static final ResourceLocation DISABLED_HORIZONTAL_VANILLA_SCROLLBAR_TEXTURE = new ResourceLocation("owo",
            "scrollbar/vanilla_horizontal_disabled");
    public static final ResourceLocation VANILLA_SCROLLBAR_TRACK_TEXTURE = new ResourceLocation("owo",
            "scrollbar/track");
    public static final ResourceLocation FLAT_VANILLA_SCROLLBAR_TEXTURE = new ResourceLocation("owo",
            "scrollbar/vanilla_flat");

    protected double scrollOffset = 0;
    protected double currentScrollPosition = 0;
    protected int lastScrollPosition = -1;
    /**
     * The increment, or step size, this container should scroll
     * by. If this is anything other than {@code 0}, all scrolling in
     * this container will snap to the closest multiple of this value
     */
    @Getter
    @Setter
    protected int scrollStep = 0;

    /**
     * The current fixed length of this container's scrollbar, or {@code 0} if it adjusts based on the content
     */
    @Getter
    @Setter
    protected int fixedScrollbarLength = 0;
    protected double lastScrollbarLength = 0;

    /**
     * The scrollbar this container should display. To create one,
     * look at the static methods on {@link Scrollbar} or use a lambda
     */
    @Getter
    @Setter
    protected Scrollbar scrollbar = Scrollbar.flat(Color.ofArgb(0xA0000000));
    /**
     * Set the thickness of this container's scrollbar,
     * in logical pixels
     */
    @Getter
    @Setter
    protected int scrollbarThickness = 3;

    protected long lastScrollbarInteractTime = 0;
    protected int scrollbarOffset = 0;
    protected boolean scrollbaring = false;

    protected int maxScroll = 0;
    protected int childSize = 0;

    protected final ScrollDirection direction;

    protected ScrollContainer(ScrollDirection direction, Sizing horizontalSizing, Sizing verticalSizing, C child) {
        super(horizontalSizing, verticalSizing, child);
        this.direction = direction;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        if (this.direction == ScrollDirection.VERTICAL) {
            return super.determineHorizontalContentSize(sizing);
        } else {
            throw new UnsupportedOperationException("Horizontal ScrollContainer cannot be horizontally content-sized");
        }
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        if (this.direction == ScrollDirection.HORIZONTAL) {
            return super.determineVerticalContentSize(sizing);
        } else {
            throw new UnsupportedOperationException("Vertical ScrollContainer cannot be vertically content-sized");
        }
    }

    @Override
    public void layout(Size space) {
        super.layout(space);

        this.maxScroll = Math.max(0, this.direction.sizeGetter.apply(child) -
                (this.direction.sizeGetter.apply(this) - this.direction.insetGetter.apply(this.padding.get())));
        this.scrollOffset = Mth.clamp(this.scrollOffset, 0, this.maxScroll + .5);
        this.childSize = this.direction.sizeGetter.apply(this.child);
        this.lastScrollPosition = -1;
    }

    @Override
    protected int childMountX() {
        return (int) (super.childMountX() - this.direction.choose(this.currentScrollPosition, 0));
    }

    @Override
    protected int childMountY() {
        return (int) (super.childMountY() - this.direction.choose(0, this.currentScrollPosition));
    }

    @Override
    protected void parentUpdate(float delta, int mouseX, int mouseY) {
        super.parentUpdate(delta, mouseX, mouseY);
        this.currentScrollPosition += Delta.compute(this.currentScrollPosition, this.scrollOffset, delta * .5);
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);

        // Update child
        int effectiveScrollOffset = this.scrollStep > 0 ?
                ((int) this.scrollOffset / this.scrollStep) * this.scrollStep : (int) this.currentScrollPosition;
        if (this.scrollStep > 0 && this.maxScroll - this.scrollOffset == -1) {
            effectiveScrollOffset += this.scrollOffset % this.scrollStep;
        }

        int newScrollPosition = this.direction.coordinateGetter.apply(this) - effectiveScrollOffset;
        if (newScrollPosition != this.lastScrollPosition) {
            this.direction.coordinateSetter.accept(this.child,
                    newScrollPosition + (this.direction == ScrollDirection.VERTICAL ?
                            this.padding.get().top() + this.child.margins().get().top() :
                            this.padding.get().left() + this.child.margins().get().left()));
            this.lastScrollPosition = newScrollPosition;
        }

        // Draw, adding the fractional part of the offset via matrix translation
        graphics.pose().pushPose();

        double visualOffset = -(this.currentScrollPosition % 1d);
        if (visualOffset > 9999999e-7 || visualOffset < .1e-6) visualOffset = 0;

        graphics.pose().translate(this.direction.choose(visualOffset, 0), this.direction.choose(0, visualOffset), 0);
        this.drawChildren(graphics, mouseX, mouseY, partialTicks, delta, this.childView);

        graphics.pose().popPose();

        // -----

        // Highlight the scrollbar if it's being hovered
        if (this.isInScrollbar(mouseX, mouseY) || this.scrollbaring) {
            this.lastScrollbarInteractTime = System.currentTimeMillis() + 1500;
        }

        var padding = this.padding.get();
        int selfSize = this.direction.sizeGetter.apply(this);
        int contentSize = this.direction.sizeGetter.apply(this) - this.direction.insetGetter.apply(padding);

        // Determine the offset of the scrollbar on the
        // *opposite* axis to the one we scroll on
        this.scrollbarOffset = this.direction == ScrollDirection.VERTICAL ?
                this.x + this.width - padding.right() - scrollbarThickness :
                this.y + this.height - padding.bottom() - scrollbarThickness;

        this.lastScrollbarLength = this.fixedScrollbarLength == 0 ?
                Math.min(Math.floor(((float) selfSize / this.childSize) * contentSize), contentSize) :
                this.fixedScrollbarLength;
        double scrollbarPosition = this.maxScroll != 0 ?
                (this.currentScrollPosition / this.maxScroll) * (contentSize - this.lastScrollbarLength) : 0;

        if (this.direction == ScrollDirection.VERTICAL) {
            this.scrollbar.draw(graphics,
                    this.scrollbarOffset,
                    (int) (this.y + scrollbarPosition + padding.top()),
                    this.scrollbarThickness,
                    (int) (this.lastScrollbarLength),
                    this.scrollbarOffset, this.y + padding.top(),
                    this.scrollbarThickness, this.height - padding.vertical(),
                    lastScrollbarInteractTime, this.direction,
                    this.maxScroll > 0);
        } else {
            this.scrollbar.draw(graphics,
                    (int) (this.x + scrollbarPosition + padding.left()),
                    this.scrollbarOffset,
                    (int) (this.lastScrollbarLength),
                    this.scrollbarThickness,
                    this.x + padding.left(), this.scrollbarOffset,
                    this.width - padding.horizontal(), this.scrollbarThickness,
                    lastScrollbarInteractTime, this.direction,
                    this.maxScroll > 0);
        }
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        if (this.child.onMouseScroll(this.x + mouseX - this.child.x(), this.y + mouseY - this.child.y(), amount))
            return true;

        if (this.scrollStep < 1) {
            this.scrollBy(-amount * 15, false, true);
        } else {
            this.scrollBy(-amount * this.scrollStep, true, true);
        }

        return true;
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (this.isInScrollbar(this.x + mouseX, this.y + mouseY)) {
            super.onMouseDown(mouseX, mouseY, button);
            return true;
        } else {
            return super.onMouseDown(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        if (!this.scrollbaring && !this.isInScrollbar(this.x + mouseX, this.y + mouseY))
            return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);

        double delta = this.direction.choose(deltaX, deltaY);
        double selfSize = this.direction.sizeGetter.apply(this) - this.direction.insetGetter.apply(this.padding.get());
        double scalar = (this.maxScroll) / (selfSize - this.lastScrollbarLength);
        if (!Double.isFinite(scalar)) scalar = 0;

        this.scrollBy(delta * scalar, true, false);
        this.scrollbaring = true;

        return true;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == this.direction.lessKeycode) {
            this.scrollBy(-10, false, true);
        } else if (keyCode == this.direction.moreKeycode) {
            this.scrollBy(10, false, true);
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            this.scrollBy(this.direction.choose(this.width, this.height) * .8, false, true);
            this.lastScrollbarInteractTime = System.currentTimeMillis() + 1250;
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            this.scrollBy(this.direction.choose(this.width, this.height) * -.8, false, true);
        }

        return false;
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        this.scrollbaring = false;
        return true;
    }

    @Override
    public @Nullable UIComponent childAt(int x, int y) {
        if (this.isInScrollbar(x, y)) {
            return this;
        } else {
            return super.childAt(x, y);
        }
    }

    protected void scrollBy(double offset, boolean instant, boolean showScrollbar) {
        this.scrollOffset = Mth.clamp(this.scrollOffset + offset, 0, this.maxScroll + .5);
        if (instant) this.currentScrollPosition = this.scrollOffset;
        if (showScrollbar) this.lastScrollbarInteractTime = System.currentTimeMillis() + 1250;
    }

    protected boolean isInScrollbar(double mouseX, double mouseY) {
        return this.isInBoundingBox(mouseX, mouseY) && this.direction.choose(mouseY, mouseX) >= this.scrollbarOffset;
    }

    /**
     * Scroll to the given component
     */
    public ScrollContainer<C> scrollTo(UIComponent component) {
        if (this.direction == ScrollDirection.VERTICAL) {
            this.scrollOffset = Mth.clamp(
                    this.scrollOffset - (this.y - component.y() + component.margins().get().top()), 0, this.maxScroll);
        } else {
            this.scrollOffset = Mth.clamp(
                    this.scrollOffset - (this.x - component.x() + component.margins().get().right()), 0,
                    this.maxScroll);
        }
        return this;
    }

    /**
     * Scroll to the specified point along the entire
     * length of this container's content
     */
    public ScrollContainer<C> scrollTo(@Range(from = 0, to = 1) double progress) {
        this.scrollOffset = this.maxScroll * progress;
        return this;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "fixed-scrollbar-length", UIParsing::parseUnsignedInt, this::fixedScrollbarLength);
        UIParsing.apply(children, "scrollbar-thickness", UIParsing::parseUnsignedInt, this::scrollbarThickness);
        UIParsing.apply(children, "scrollbar", Scrollbar::parse, this::scrollbar);

        UIParsing.apply(children, "scroll-step", UIParsing::parseUnsignedInt, this::scrollStep);
    }

    public static ScrollContainer<?> parse(Element element) {
        return element.getAttribute("direction").equals("vertical") ?
                UIContainers.verticalScroll(Sizing.content(), Sizing.content(), null) :
                UIContainers.horizontalScroll(Sizing.content(), Sizing.content(), null);
    }

    @FunctionalInterface
    public interface Scrollbar {

        /**
         * A rectangular scrollbar filled with the given color
         */
        static Scrollbar flat(Color color) {
            int scrollbarColor = color.argb();

            return (graphics, x, y, width, height, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction,
                    active) -> {
                if (!active) return;

                final var progress = Easing.SINE
                        .apply(Mth.clamp(lastInteractTime - System.currentTimeMillis(), 0, 750) / 750f);
                int alpha = (int) (progress * (scrollbarColor >>> 24));

                graphics.fill(
                        x, y, x + width, y + height,
                        alpha << 24 | (scrollbarColor & 0xFFFFFF));
            };
        }

        /**
         * The vanilla scrollbar used by the creative inventory screen
         */
        static Scrollbar vanilla() {
            return (graphics, x, y, width, height, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction,
                    active) -> {
                NinePatchTexture.draw(VANILLA_SCROLLBAR_TRACK_TEXTURE, graphics, trackX, trackY, trackWidth,
                        trackHeight);

                var texture = direction == ScrollDirection.VERTICAL ?
                        active ? VERTICAL_VANILLA_SCROLLBAR_TEXTURE : DISABLED_VERTICAL_VANILLA_SCROLLBAR_TEXTURE :
                        active ? HORIZONTAL_VANILLA_SCROLLBAR_TEXTURE : DISABLED_HORIZONTAL_VANILLA_SCROLLBAR_TEXTURE;

                NinePatchTexture.draw(texture, graphics, x + 1, y + 1, width - 2, height - 2);
            };
        }

        /**
         * The vanilla scrollbar used by the creative inventory screen
         */
        static Scrollbar custom(ResourceLocation track, ResourceLocation background) {
            return (graphics, x, y, width, height, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction,
                    active) -> {
                NinePatchTexture.draw(track, graphics, trackX, trackY, trackWidth, trackHeight);
                NinePatchTexture.draw(background, graphics, x + 1, y + 1, width - 2, height - 2);
            };
        }

        /**
         * The more flat looking vanilla scrollbar used in the
         * game options screens
         */
        static Scrollbar vanillaFlat() {
            return (graphics, x, y, width, height, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction,
                    active) -> {
                graphics.fill(trackX, trackY, trackX + trackWidth, trackY + trackHeight, Color.BLACK.argb());
                NinePatchTexture.draw(FLAT_VANILLA_SCROLLBAR_TEXTURE, graphics, x, y, width, height);
            };
        }

        void draw(UIGuiGraphics graphics, int x, int y, int width, int height, int trackX, int trackY, int trackWidth,
                  int trackHeight,
                  long lastInteractTime, ScrollDirection direction, boolean active);

        static Scrollbar parse(Element element) {
            var children = UIParsing.<Element>allChildrenOfType(element, Node.ELEMENT_NODE);
            if (children.size() > 1)
                throw new UIModelParsingException("'scrollbar' declaration may only contain a single child");

            var scrollbarElement = children.get(0);
            return switch (scrollbarElement.getNodeName()) {
                case "vanilla" -> vanilla();
                case "vanilla-flat" -> vanillaFlat();
                case "flat" -> flat(Color.parse(scrollbarElement));
                default -> throw new UIModelParsingException(
                        "Unknown scrollbar type '" + scrollbarElement.getNodeName() + "'");
            };
        }
    }

    public enum ScrollDirection {

        VERTICAL(UIComponent::height, UIComponent::y, UIComponent::y, Insets::vertical, GLFW.GLFW_KEY_UP,
                GLFW.GLFW_KEY_DOWN),
        HORIZONTAL(UIComponent::width, UIComponent::x, UIComponent::x, Insets::horizontal, GLFW.GLFW_KEY_LEFT,
                GLFW.GLFW_KEY_RIGHT);

        public final Function<UIComponent, Integer> sizeGetter;
        public final BiConsumer<UIComponent, Integer> coordinateSetter;
        public final Function<ScrollContainer<?>, Integer> coordinateGetter;
        public final Function<Insets, Integer> insetGetter;

        public final int lessKeycode, moreKeycode;

        ScrollDirection(Function<UIComponent, Integer> sizeGetter, BiConsumer<UIComponent, Integer> coordinateSetter,
                        Function<ScrollContainer<?>, Integer> coordinateGetter, Function<Insets, Integer> insetGetter,
                        int lessKeycode, int moreKeycode) {
            this.sizeGetter = sizeGetter;
            this.coordinateSetter = coordinateSetter;
            this.coordinateGetter = coordinateGetter;
            this.insetGetter = insetGetter;
            this.lessKeycode = lessKeycode;
            this.moreKeycode = moreKeycode;
        }

        public double choose(double horizontal, double vertical) {
            return switch (this) {
                case VERTICAL -> vertical;
                case HORIZONTAL -> horizontal;
            };
        }
    }
}
