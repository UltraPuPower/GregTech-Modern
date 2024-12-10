package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.component.LabelComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.Delta;
import com.gregtechceu.gtceu.api.ui.util.EventSource;
import com.gregtechceu.gtceu.api.ui.util.EventStream;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.mojang.math.Axis;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollapsibleContainer extends FlowLayout {

    public static final Surface SURFACE = (context, component) -> context.fill(
            component.x() + 5,
            component.y(),
            component.x() + 6,
            component.y() + component.height(),
            0x77FFFFFF);

    protected final EventStream<OnToggled> toggledEvents = OnToggled.newStream();

    protected final List<UIComponent> collapsibleChildren = new ArrayList<>();
    protected final List<UIComponent> collapsibleChildrenView = Collections.unmodifiableList(this.collapsibleChildren);
    protected boolean expanded;

    protected final SpinnyBoiComponent spinnyBoi;
    protected final FlowLayout titleLayout;
    protected final FlowLayout contentLayout;

    protected CollapsibleContainer(Sizing horizontalSizing, Sizing verticalSizing, Component title, boolean expanded) {
        super(horizontalSizing, verticalSizing, Algorithm.VERTICAL);

        // Title

        this.titleLayout = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        this.titleLayout.padding(Insets.of(5, 5, 5, 0));
        this.allowOverflow(true);

        title = title.copy().withStyle(ChatFormatting.UNDERLINE);
        this.titleLayout.child(UIComponents.label(title).cursorStyle(CursorStyle.HAND));

        this.spinnyBoi = new SpinnyBoiComponent();
        this.titleLayout.child(spinnyBoi);

        this.expanded = expanded;
        this.spinnyBoi.targetRotation = expanded ? 90 : 0;
        this.spinnyBoi.rotation = this.spinnyBoi.targetRotation;

        super.child(this.titleLayout);

        // Content

        this.contentLayout = UIContainers.verticalFlow(Sizing.content(), Sizing.content());
        this.contentLayout.padding(Insets.left(15));
        this.contentLayout.surface(SURFACE);

        super.child(this.contentLayout);
    }

    public FlowLayout titleLayout() {
        return this.titleLayout;
    }

    public List<UIComponent> collapsibleChildren() {
        return this.collapsibleChildrenView;
    }

    public boolean expanded() {
        return this.expanded;
    }

    public EventSource<OnToggled> onToggled() {
        return this.toggledEvents.source();
    }

    public void toggleExpansion() {
        if (expanded) {
            this.contentLayout.clearChildren();
            this.spinnyBoi.targetRotation = 0;
        } else {
            this.contentLayout.children(this.collapsibleChildren);
            this.spinnyBoi.targetRotation = 90;
        }

        this.expanded = !this.expanded;
        this.toggledEvents.sink().onToggle(this.expanded);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.KEYBOARD_CYCLE;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.toggleExpansion();

            super.onKeyPress(keyCode, scanCode, modifiers);
            return true;
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        final var superResult = super.onMouseDown(mouseX, mouseY, button);

        if (mouseY <= this.titleLayout.fullSize().height() && !superResult) {
            this.toggleExpansion();
            return true;
        } else {
            return superResult;
        }
    }

    @Override
    public FlowLayout child(UIComponent child) {
        this.collapsibleChildren.add(child);
        child.setContainerAccess(this.parentAccess);
        if (this.expanded) this.contentLayout.child(child);
        return this;
    }

    @Override
    public FlowLayout children(Collection<? extends UIComponent> children) {
        this.collapsibleChildren.addAll(children);
        if (this.expanded) this.contentLayout.children(children);
        return this;
    }

    @Override
    public FlowLayout child(int index, UIComponent child) {
        this.collapsibleChildren.add(index, child);
        if (this.expanded) this.contentLayout.child(index, child);
        return this;
    }

    @Override
    public FlowLayout children(int index, Collection<? extends UIComponent> children) {
        this.collapsibleChildren.addAll(index, children);
        if (this.expanded) this.contentLayout.children(index, children);
        return this;
    }

    @Override
    public FlowLayout removeChild(UIComponent child) {
        this.collapsibleChildren.remove(child);
        return this.contentLayout.removeChild(child);
    }

    public static CollapsibleContainer parse(Element element) {
        var textElement = UIParsing.childElements(element).get("text");
        var title = textElement == null ? Component.empty() : UIParsing.parseText(textElement);

        return element.getAttribute("expanded").equals("true") ?
                UIContainers.collapsible(Sizing.content(), Sizing.content(), title, true) :
                UIContainers.collapsible(Sizing.content(), Sizing.content(), title, false);
    }

    public interface OnToggled {

        void onToggle(boolean nowExpanded);

        static EventStream<OnToggled> newStream() {
            return new EventStream<>(subscribers -> nowExpanded -> {
                for (var subscriber : subscribers) {
                    subscriber.onToggle(nowExpanded);
                }
            });
        }
    }

    protected static class SpinnyBoiComponent extends LabelComponent {

        protected float rotation = 90;
        protected float targetRotation = 90;

        public SpinnyBoiComponent() {
            super(Component.literal(">"));
            this.margins(Insets.of(0, 0, 5, 10));
            this.cursorStyle(CursorStyle.HAND);
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            super.update(delta, mouseX, mouseY);
            this.rotation += Delta.compute(this.rotation, this.targetRotation, delta * .65);
        }

        @Override
        public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
            var pose = graphics.pose();

            pose.pushPose();
            pose.translate(this.x + this.width / 2f - 1, this.y + this.height / 2f - 1, 0);
            pose.mulPose(Axis.ZP.rotationDegrees(this.rotation));
            pose.translate(-(this.x + this.width / 2f - 1), -(this.y + this.height / 2f - 1), 0);

            super.draw(graphics, mouseX, mouseY, partialTicks, delta);
            pose.popPose();
        }
    }
}
