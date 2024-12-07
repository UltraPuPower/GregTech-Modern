package com.gregtechceu.gtceu.ui.component;

import com.gregtechceu.gtceu.ui.core.Sizing;
import com.gregtechceu.gtceu.ui.parsing.UIModel;
import com.gregtechceu.gtceu.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.ui.util.EventSource;
import com.gregtechceu.gtceu.ui.util.EventStream;
import com.lowdragmc.shimmer.client.auxiliaryScreen.SliderWidget;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.Function;

public class SliderComponent extends AbstractSliderButton {
    protected final EventStream<OnChanged> changedEvents = OnChanged.newStream();
    protected final EventStream<OnSlideEnd> slideEndEvents = OnSlideEnd.newStream();

    protected Function<String, Component> messageProvider = value -> Component.empty();
    protected double scrollStep = .05;

    protected SliderComponent(Sizing horizontalSizing) {
        super(0, 0, 0, 0, Component.empty(), 0);

        this.sizing(horizontalSizing, Sizing.fixed(20));
    }

    public SliderComponent value(double value) {
        value = Mth.clamp(value, 0, 1);

        if (this.value != value) {
            this.value = value;

            this.updateMessage();
            this.applyValue();
        }

        return this;
    }

    public double value() {
        return this.value;
    }

    public SliderComponent message(Function<String, Component> messageProvider) {
        this.messageProvider = messageProvider;
        this.updateMessage();
        return this;
    }

    public SliderComponent scrollStep(double scrollStep) {
        this.scrollStep = scrollStep;
        return this;
    }

    public double scrollStep() {
        return this.scrollStep;
    }

    public SliderComponent active(boolean active) {
        this.active = active;
        return this;
    }

    public boolean active() {
        return this.active;
    }

    public EventSource<OnChanged> onChanged() {
        return this.changedEvents.source();
    }

    public EventSource<OnSlideEnd> slideEnd() {
        return this.slideEndEvents.source();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.messageProvider.apply(String.valueOf(this.value)));
    }

    @Override
    protected void applyValue() {
        this.changedEvents.sink().onChanged(this.value);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        if (!this.active) return super.onMouseScroll(mouseX, mouseY, amount);

        this.value(this.value + this.scrollStep * amount);

        super.onMouseScroll(mouseX, mouseY, amount);
        return true;
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        this.slideEndEvents.sink().onSlideEnd();
        return super.onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active) return false;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return this.active && super.isValidClickButton(button);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        if (children.containsKey("text")) {
            var node = children.get("text");
            var content = node.getTextContent().strip();

            if (node.getAttribute("translate").equalsIgnoreCase("true")) {
                this.message(value -> Component.translatable(content, value));
            } else {
                var text = Component.literal(content);
                this.message(value -> text);
            }
        }

        UIParsing.apply(children, "value", UIParsing::parseDouble, this::value);
    }

    /**
     * @deprecated Use {@link #message(Function)} instead,
     * as the message set by this method will be overwritten
     * the next time this slider is moved
     */
    @Override
    @Deprecated
    public final void setMessage(Component message) {
        super.setMessage(message);
    }

    public interface OnChanged {
        void onChanged(double value);

        static EventStream<OnChanged> newStream() {
            return new EventStream<>(subscribers -> value -> {
                for (var subscriber : subscribers) {
                    subscriber.onChanged(value);
                }
            });
        }
    }

    public interface OnSlideEnd {
        void onSlideEnd();

        static EventStream<OnSlideEnd> newStream() {
            return new EventStream<>(subscribers -> () -> {
                for (var subscriber : subscribers) {
                    subscriber.onSlideEnd();
                }
            });
        }
    }
}
