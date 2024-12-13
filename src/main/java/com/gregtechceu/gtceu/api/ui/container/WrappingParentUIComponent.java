package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.base.BaseParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Size;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelParsingException;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WrappingParentUIComponent<C extends UIComponent> extends BaseParentUIComponent {

    protected C child;
    protected List<UIComponent> childView;

    protected WrappingParentUIComponent(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        super(horizontalSizing, verticalSizing);
        this.child = child;
        if (this.child != null) {
            this.child.containerAccess(this.parentAccess);
        }
        this.childView = Collections.singletonList(this.child);
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.child.fullSize().width() + this.padding.get().horizontal();
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.child.fullSize().height() + this.padding.get().vertical();
    }

    @Override
    public void layout(Size space) {
        this.child.inflate(this.calculateChildSpace(space));
        this.child.mount(this, this.childMountX(), this.childMountY());
    }

    /**
     * @return The x-coordinate at which to mount the child
     */
    protected int childMountX() {
        return this.x + child.margins().get().left() + this.padding.get().left();
    }

    /**
     * @return The y-coordinate at which to mount the child
     */
    protected int childMountY() {
        return this.y + child.margins().get().top() + this.padding.get().top();
    }

    public WrappingParentUIComponent<C> child(C newChild) {
        if (this.child != null) {
            this.child.dismount(DismountReason.REMOVED);
            this.child.containerAccess(null);
        }

        this.child = newChild;
        this.child.containerAccess(this.parentAccess);
        this.childView = Collections.singletonList(this.child);

        this.updateLayout();
        return this;
    }

    public C child() {
        return this.child;
    }

    @Override
    public List<UIComponent> children() {
        return this.childView;
    }

    @Override
    public ParentUIComponent removeChild(UIComponent child) {
        throw new UnsupportedOperationException("Cannot remove the child of a wrapping component");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        try {
            var childList = UIParsing.<Element>allChildrenOfType(element, Node.ELEMENT_NODE);
            this.child((C) model.parseComponent(UIComponent.class, childList.get(0)));
        } catch (UIModelParsingException exception) {
            throw new UIModelParsingException("Could not initialize container child", exception);
        }
    }
}
