package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.base.BaseParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.MountingHelper;
import com.gregtechceu.gtceu.api.ui.util.Observable;
import org.apache.commons.lang3.mutable.MutableInt;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;


public class FlowLayout extends BaseParentUIComponent {

    protected final List<UIComponent> children = new ArrayList<>();
    protected final List<UIComponent> childrenView = Collections.unmodifiableList(this.children);
    protected final Algorithm algorithm;

    protected Size contentSize = Size.zero();
    protected Observable<Integer> gap = Observable.of(0);

    protected FlowLayout(Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm) {
        super(horizontalSizing, verticalSizing);
        this.algorithm = algorithm;

        this.gap.observe(integer -> this.updateLayout());
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.contentSize.width() + this.padding.get().horizontal();
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.contentSize.height() + this.padding.get().vertical();
    }

    @Override
    public void layout(Size space) {
        this.algorithm.layout(this);
    }

    /**
     * Add a single child to this layout. If you need to add multiple
     * children, use {@link #children(Collection)} instead
     *
     * @param child The child to append to this layout
     */
    public FlowLayout child(UIComponent child) {
        this.children.add(child);
        this.updateLayout();
        return this;
    }

    /**
     * Add a collection of children to this layout. If you only need to
     * add a single child to, use {@link #child(UIComponent)} instead
     *
     * @param children The children to add to this layout
     */
    public FlowLayout children(Collection<? extends UIComponent> children) {
        this.children.addAll(children);
        this.updateLayout();
        return this;
    }

    /**
     * Insert a single child into this layout. If you need to insert multiple
     * children, use {@link #children(int, Collection)} instead
     *
     * @param index The index at which to insert the child
     * @param child The child to append to this layout
     */
    public FlowLayout child(int index, UIComponent child) {
        this.children.add(index, child);
        this.updateLayout();
        return this;
    }

    /**
     * Insert a collection of children into this layout. If you only need to
     * insert a single child to, use {@link #child(int, UIComponent)} instead
     *
     * @param index    The index at which to begin inserting children
     * @param children The children to add to this layout
     */
    public FlowLayout children(int index, Collection<? extends UIComponent> children) {
        this.children.addAll(index, children);
        this.updateLayout();
        return this;
    }

    @Override
    public FlowLayout removeChild(UIComponent child) {
        if (this.children.remove(child)) {
            child.dismount(DismountReason.REMOVED);
            this.updateLayout();
        }

        return this;
    }

    /**
     * Remove all children from this layout
     */
    public FlowLayout clearChildren() {
        for (var child : this.children) {
            child.dismount(DismountReason.REMOVED);
        }

        this.children.clear();
        this.updateLayout();

        return this;
    }

    @Override
    public List<UIComponent> children() {
        return this.childrenView;
    }

    /**
     * Set the gap, in logical pixels, this layout
     * should insert between all child components
     */
    public FlowLayout gap(int gap) {
        this.gap.set(gap);
        return this;
    }

    /**
     * @return The gap, in logical pixels, this layout
     * inserts between all child components
     */
    public int gap() {
        return this.gap.get();
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        this.drawChildren(graphics, mouseX, mouseY, partialTicks, delta, this.children);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "gap", UIParsing::parseSignedInt, this::gap);

        final var components = UIParsing
                .get(children, "children", e -> UIParsing.<Element>allChildrenOfType(e, Node.ELEMENT_NODE))
                .orElse(Collections.emptyList());

        for (var child : components) {
            this.child(model.parseComponent(UIComponent.class, child));
        }
    }

    public static FlowLayout parse(Element element) {
        UIParsing.expectAttributes(element, "direction");

        return switch (element.getAttribute("direction")) {
            case "horizontal" -> Containers.horizontalFlow(Sizing.content(), Sizing.content());
            case "ltr-text-flow" -> Containers.ltrTextFlow(Sizing.content(), Sizing.content());
            default -> Containers.verticalFlow(Sizing.content(), Sizing.content());
        };
    }

    @FunctionalInterface
    public interface Algorithm {
        void layout(FlowLayout container);

        Algorithm HORIZONTAL = container -> {
            var layoutWidth = new MutableInt(0);
            var layoutHeight = new MutableInt(0);

            final var layout = new ArrayList<UIComponent>();
            final var padding = container.padding.get();
            final var childSpace = container.calculateChildSpace(container.space);

            container.children.forEach(child -> child.inflate(childSpace));

            var mountState = MountingHelper.mountEarly(container::mountChild, container.children, child -> {
                layout.add(child);

                child.mount(container,
                        container.x + padding.left() + child.margins().get().left() + layoutWidth.intValue(),
                        container.y + padding.top() + child.margins().get().top());

                final var childSize = child.fullSize();
                layoutWidth.add(childSize.width() + container.gap());
                if (childSize.height() > layoutHeight.intValue()) {
                    layoutHeight.setValue(childSize.height());
                }
            });

            layoutWidth.subtract(container.gap());

            container.contentSize = Size.of(layoutWidth.intValue(), layoutHeight.intValue());
            container.applySizing();

            if (container.verticalAlignment() != VerticalAlignment.TOP) {
                for (var component : layout) {
                    component.updateY(component.baseY() + container.verticalAlignment().align(component.fullSize().height(), container.height - padding.vertical()));
                }
            }

            if (container.horizontalAlignment() != HorizontalAlignment.LEFT) {
                for (var component : layout) {
                    if (container.horizontalAlignment() == HorizontalAlignment.CENTER) {
                        component.updateX(component.baseX() + (container.width - padding.horizontal() - layoutWidth.intValue()) / 2);
                    } else {
                        component.updateX(component.baseX() + (container.width - padding.horizontal() - layoutWidth.intValue()));
                    }
                }
            }

            mountState.mountLate();
        };

        Algorithm VERTICAL = container -> {
            var layoutHeight = new MutableInt(0);
            var layoutWidth = new MutableInt(0);

            final var layout = new ArrayList<UIComponent>();
            final var padding = container.padding.get();
            final var childSpace = container.calculateChildSpace(container.space);

            container.children.forEach(child -> child.inflate(childSpace));

            var mountState = MountingHelper.mountEarly(container::mountChild, container.children, child -> {
                layout.add(child);

                child.mount(container,
                        container.x + padding.left() + child.margins().get().left(),
                        container.y + padding.top() + child.margins().get().top() + layoutHeight.intValue());

                final var childSize = child.fullSize();
                layoutHeight.add(childSize.height() + container.gap());
                if (childSize.width() > layoutWidth.intValue()) {
                    layoutWidth.setValue(childSize.width());
                }
            });

            layoutHeight.subtract(container.gap());

            container.contentSize = Size.of(layoutWidth.intValue(), layoutHeight.intValue());
            container.applySizing();

            if (container.horizontalAlignment() != HorizontalAlignment.LEFT) {
                for (var component : layout) {
                    component.updateX(component.baseX() + container.horizontalAlignment().align(component.fullSize().width(), container.width - padding.horizontal()));
                }
            }

            if (container.verticalAlignment() != VerticalAlignment.TOP) {
                for (var component : layout) {
                    if (container.verticalAlignment() == VerticalAlignment.CENTER) {
                        component.updateY(component.baseY() + (container.height - padding.vertical() - layoutHeight.intValue()) / 2);
                    } else {
                        component.updateY(component.baseY() + (container.height - padding.vertical() - layoutHeight.intValue()));
                    }
                }
            }

            mountState.mountLate();
        };

        Algorithm LTR_TEXT = container -> {
            if (container.horizontalSizing.get().isContent()) {
                throw new IllegalStateException("An LTR-text-flow layout must use content-independent horizontal sizing");
            }

            var layoutWidth = new MutableInt(0);
            var layoutHeight = new MutableInt(0);

            var rowWidth = new MutableInt(0);
            var rowOffset = new MutableInt(0);

            final var layout = new ArrayList<UIComponent>();
            final var padding = container.padding.get();
            final var childSpace = container.calculateChildSpace(container.space);

            container.children.forEach(child -> child.inflate(childSpace));

            var mountState = MountingHelper.mountEarly(container::mountChild, container.children, child -> {
                layout.add(child);

                int x = container.x + padding.left() + child.margins().get().left() + rowWidth.intValue();
                int y = container.y + padding.top() + child.margins().get().top() + rowOffset.intValue();

                final var childSize = child.fullSize();
                if (rowWidth.intValue() + childSize.width() > childSpace.width()) {
                    x -= rowWidth.intValue();
                    y = y - rowOffset.intValue() + layoutHeight.intValue();

                    rowOffset.setValue(layoutHeight);
                    rowWidth.setValue(0);
                }

                child.mount(container, x, y);

                rowWidth.add(childSize.width() + container.gap());
                if (rowOffset.intValue() + childSize.height() > layoutHeight.intValue()) {
                    layoutHeight.setValue(rowOffset.intValue() + childSize.height());
                }
                if (rowWidth.intValue() > layoutWidth.intValue()) {
                    layoutWidth.setValue(rowWidth.intValue());
                }
            });

            layoutWidth.subtract(container.gap());

            container.contentSize = Size.of(layoutWidth.intValue(), layoutHeight.intValue());
            container.applySizing();

            if (container.verticalAlignment() != VerticalAlignment.TOP) {
                for (var component : layout) {
                    component.updateY(component.baseY() + container.verticalAlignment().align(layoutHeight.intValue(), container.height - padding.vertical()));
                }
            }

            if (container.horizontalAlignment() != HorizontalAlignment.LEFT) {
                for (var component : layout) {
                    if (container.horizontalAlignment() == HorizontalAlignment.CENTER) {
                        component.updateX(component.baseX() + (container.width - padding.horizontal() - layoutWidth.intValue()) / 2);
                    } else {
                        component.updateX(component.baseX() + (container.width - padding.horizontal() - layoutWidth.intValue()));
                    }
                }
            }

            mountState.mountLate();
        };
    }
}
