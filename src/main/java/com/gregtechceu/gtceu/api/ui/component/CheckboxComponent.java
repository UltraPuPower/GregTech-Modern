package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.core.CursorStyle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.Observable;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.CheckboxAccessor;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.Consumer;

public class CheckboxComponent extends Checkbox {

    protected final Observable<Boolean> listeners;

    protected CheckboxComponent(Component message) {
        super(0, 0, 0, 0, message, false);
        this.listeners = Observable.of(this.selected());
        this.sizing(Sizing.content(), Sizing.fixed(20));
    }

    @Override
    public void onPress() {
        super.onPress();
        this.listeners.set(this.selected());
    }

    public CheckboxComponent checked(boolean checked) {
        ((CheckboxAccessor) this).gtceu$setSelected(checked);
        this.listeners.set(this.selected());
        return this;
    }

    public CheckboxComponent onChanged(Consumer<Boolean> listener) {
        this.listeners.observe(listener);
        return this;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "checked", UIParsing::parseBool, this::checked);
        UIParsing.apply(children, "text", UIParsing::parseText, this::setMessage);
    }

    public CursorStyle gtceu$preferredCursorStyle() {
        return CursorStyle.HAND;
    }
}
