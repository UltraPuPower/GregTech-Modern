package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.ui.container.ComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;


public interface IEditableUI<W extends UIComponent, T> {

    W createDefault();

    void setupUI(W template, T instance);

    record Normal<A extends UIComponent, B>(Supplier<A> supplier, BiConsumer<A, B> binder)
            implements IEditableUI<A, B> {

        @Override
        public A createDefault() {
            return supplier.get();
        }

        @Override
        public void setupUI(A template, B instance) {
            binder.accept(template, instance);
        }
    }
}
