package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.CursorStyle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.EventSource;
import com.gregtechceu.gtceu.api.ui.util.EventStream;
import com.gregtechceu.gtceu.api.ui.util.NinePatchTexture;
import com.gregtechceu.gtceu.api.ui.util.Observable;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SlimSliderComponent extends BaseUIComponent {

    public static final Function<Double, Component> VALUE_TOOLTIP_SUPPLIER = value -> Component
            .literal(String.valueOf(value));

    protected static final ResourceLocation TEXTURE = new ResourceLocation("owo", "textures/gui/slim_slider.png");
    protected static final ResourceLocation TRACK_TEXTURE = new ResourceLocation("owo", "slim_slider_track");

    protected final EventStream<OnChanged> changedEvents = OnChanged.newStream();
    protected final EventStream<OnSlideEnd> slideEndEvents = OnSlideEnd.newStream();

    protected final Axis axis;
    protected final Observable<Double> value = Observable.of(0d);

    protected double min = 0d, max = 1d;
    protected double stepSize = 0;
    protected @Nullable Function<Double, Component> tooltipSupplier = null;

    public SlimSliderComponent(Axis axis) {
        this.cursorStyle(CursorStyle.MOVE);

        this.axis = axis;
        this.value.observe($ -> {
            this.changedEvents.sink().onChanged(this.value());
            this.updateTooltip();
        });
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        if (this.axis == Axis.VERTICAL) {
            return 9;
        } else {
            throw new UnsupportedOperationException(
                    "Horizontal SlimSliderComponent cannot be horizontally content-sized");
        }
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        if (this.axis == Axis.HORIZONTAL) {
            return 9;
        } else {
            throw new UnsupportedOperationException("Vertical SlimSliderComponent cannot be vertically content-sized");
        }
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.axis == Axis.HORIZONTAL) {
            NinePatchTexture.draw(TRACK_TEXTURE, graphics, this.x + 1, this.y + 3, this.width - 2, 3);
            graphics.blit(TEXTURE, (int) (this.x + (this.width - 4) * this.value.get()), this.y + 1, 4, 7, 0, 3, 4, 7,
                    16, 16);
        } else {
            NinePatchTexture.draw(TRACK_TEXTURE, graphics, this.x + 3, this.y + 1, 3, this.height - 2);
            graphics.blit(TEXTURE, this.x + 1, (int) (this.y + (this.height - 4) * this.value.get()), 7, 4, 4, 3, 7, 4,
                    16, 16);
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        super.onMouseDown(mouseX, mouseY, button);
        this.setValueFromMouse(mouseX, mouseY);
        return true;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
        this.setValueFromMouse(mouseX, mouseY);
        return true;
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        super.onMouseUp(mouseX, mouseY, button);
        this.slideEndEvents.sink().onSlideEnd();
        return true;
    }

    protected void setValueFromMouse(double mouseX, double mouseY) {
        this.value(this.axis == Axis.VERTICAL ? this.min + (mouseY / this.height) * (this.max - this.min) :
                this.min + (mouseX / this.width) * (this.max - this.min));
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    public EventSource<OnChanged> onChanged() {
        return this.changedEvents.source();
    }

    public EventSource<OnSlideEnd> onSlideEnd() {
        return this.slideEndEvents.source();
    }

    public SlimSliderComponent value(double value) {
        value -= this.min;
        if (this.stepSize != 0) {
            value = Math.round(value / this.stepSize) * this.stepSize;
        }

        this.value.set(Mth.clamp(value / (this.max - this.min), 0, 1));
        return this;
    }

    public double value() {
        return this.min + this.value.get() * (this.max - this.min);
    }

    public SlimSliderComponent min(double min) {
        this.min = min;
        return this;
    }

    public double min() {
        return min;
    }

    public SlimSliderComponent max(double max) {
        this.max = max;
        return this;
    }

    public double max() {
        return max;
    }

    public SlimSliderComponent stepSize(double stepSize) {
        this.stepSize = stepSize;
        return this;
    }

    public double stepSize() {
        return stepSize;
    }

    public SlimSliderComponent tooltipSupplier(Function<Double, Component> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        this.updateTooltip();

        return this;
    }

    public Function<Double, Component> tooltipSupplier() {
        return tooltipSupplier;
    }

    protected void updateTooltip() {
        if (this.tooltipSupplier != null) {
            this.tooltip(this.tooltipSupplier.apply(this.value()));
        } else {
            this.tooltip((List<ClientTooltipComponent>) null);
        }
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "step-size", UIParsing::parseDouble, this::stepSize);
        UIParsing.apply(children, "min", UIParsing::parseDouble, this::min);
        UIParsing.apply(children, "max", UIParsing::parseDouble, this::max);
        UIParsing.apply(children, "value", UIParsing::parseDouble, this::value);
    }

    public static UIComponent parse(Element element) {
        return element.getAttribute("direction").equals("vertical") ? new SlimSliderComponent(Axis.VERTICAL) :
                new SlimSliderComponent(Axis.HORIZONTAL);
    }

    public static Function<Double, Component> valueTooltipSupplier(int decimalPlaces) {
        return value -> Component
                .literal(new BigDecimal(value).setScale(decimalPlaces, RoundingMode.HALF_UP).toPlainString());
    }

    public enum Axis {
        VERTICAL,
        HORIZONTAL
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
