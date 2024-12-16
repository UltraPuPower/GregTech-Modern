package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.base.BaseParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

public class UIComponentGroup extends BaseParentUIComponent {

    protected final List<UIComponent> children = new ArrayList<>();
    protected final List<UIComponent> childrenView = Collections.unmodifiableList(this.children);

    protected Size contentSize = Size.zero();

    protected UIComponentGroup(Sizing horizontalSizing, Sizing verticalSizing) {
        super(horizontalSizing, verticalSizing);
        horizontalAlignment.set(HorizontalAlignment.RIGHT);
        verticalAlignment.set(VerticalAlignment.TOP);
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
        this.children.forEach(child -> {
            child.inflate(calculateChildSpace(space));
            this.mountChild(child, ch -> {
                ch.mount(
                        this,
                        ch.x() + child.margins().get().left() +
                                this.horizontalAlignment().align(child.fullSize().width(),
                                        this.width - padding().get().horizontal()),
                        ch.y() + child.margins().get().top() +
                                this.verticalAlignment().align(child.fullSize().height(),
                                        this.height - padding.get().vertical()));
            });
        });
    }

    /**
     * Add a single child to this layout. If you need to add multiple
     * children, use {@link #children(Collection)} instead
     *
     * @param child The child to append to this layout
     */
    public UIComponentGroup child(UIComponent child) {
        this.children.add(child);
        child.containerAccess(this.parentAccess);
        this.updateLayout();
        return this;
    }

    /**
     * Add a collection of children to this layout. If you only need to
     * add a single child to, use {@link #child(UIComponent)} instead
     *
     * @param children The children to add to this layout
     */
    public UIComponentGroup children(Collection<? extends UIComponent> children) {
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
    public UIComponentGroup child(int index, UIComponent child) {
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
    public UIComponentGroup children(int index, Collection<? extends UIComponent> children) {
        this.children.addAll(index, children);
        this.updateLayout();
        return this;
    }

    @Override
    public UIComponentGroup removeChild(UIComponent child) {
        if (this.children.remove(child)) {
            child.dismount(DismountReason.REMOVED);
            child.containerAccess(null);
            this.updateLayout();
        }

        return this;
    }

    /**
     * Remove all children from this layout
     */
    public UIComponentGroup clearChildren() {
        for (var child : this.children) {
            child.dismount(DismountReason.REMOVED);
            child.containerAccess(null);
        }

        this.children.clear();
        this.updateLayout();

        return this;
    }

    @Override
    public List<UIComponent> children() {
        return this.childrenView;
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        this.drawChildren(graphics, mouseX, mouseY, partialTicks, delta, this.children);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        final var components = UIParsing
                .get(children, "children", e -> UIParsing.<Element>allChildrenOfType(e, Node.ELEMENT_NODE))
                .orElse(Collections.emptyList());

        for (var child : components) {
            this.child(model.parseComponent(UIComponent.class, child));
        }
    }
}
