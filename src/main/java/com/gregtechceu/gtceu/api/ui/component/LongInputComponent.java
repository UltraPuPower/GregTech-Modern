package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.gui.widget.NumberInputWidget;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.utils.GTMath;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A widget containing an integer input field, as well as adjacent buttons for increasing or decreasing the value.
 *
 * <p>
 * The buttons' change amount can be altered with Ctrl, Shift, or both.<br>
 * The input is limited by a minimum and maximum value.
 * </p>
 */
public class LongInputComponent extends NumberInputComponent<Long> {

    public LongInputComponent(Supplier<Long> valueSupplier, Consumer<Long> onChanged) {
        super(valueSupplier, onChanged);
    }

    public LongInputComponent(Sizing horizontalSizing, Sizing verticalSizing, Supplier<Long> valueSupplier, Consumer<Long> onChanged) {
        super(horizontalSizing, verticalSizing, valueSupplier, onChanged);
    }

    @Override
    protected Long defaultMin() {
        return 0L;
    }

    @Override
    protected Long defaultMax() {
        return Long.MAX_VALUE;
    }

    @Override
    protected String toText(Long value) {
        return String.valueOf(value);
    }

    @Override
    protected Long fromText(String value) {
        return Long.parseLong(value);
    }

    @Override
    protected ChangeValues<Long> getChangeValues() {
        return new ChangeValues<>(1L, 8L, 64L, 512L);
    }

    @Override
    protected Long add(Long a, Long b) {
        return a + b;
    }

    @Override
    protected Long multiply(Long a, Long b) {
        return a * b;
    }

    @Override
    protected Long clamp(Long value, Long min, Long max) {
        return GTMath.clamp(value, min, max);
    }

    @Override
    protected void setTextFieldRange(TextBoxComponent textField, Long min, Long max) {
        textField.setNumbersOnly(min, max);
    }

    @Override
    protected Long getOne(boolean positive) {
        return positive ? 1L : -1L;
    }
}
