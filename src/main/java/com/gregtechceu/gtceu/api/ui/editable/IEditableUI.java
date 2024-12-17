package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import java.util.function.Supplier;

public interface IEditableUI<W extends UIComponent, T> {

    W createDefault();

    void setupUI(StackLayout template, UIAdapter<StackLayout> adapter, T instance);

    record Normal<A extends UIComponent, B>(Supplier<A> supplier, BinderFunction<StackLayout, B> binder)
            implements IEditableUI<A, B> {

        @Override
        public A createDefault() {
            return supplier.get();
        }

        @Override
        public void setupUI(StackLayout template, UIAdapter<StackLayout> adapter, B instance) {
            binder.bind(template, adapter, instance);
        }
    }

    @FunctionalInterface
    interface BinderFunction<W extends UIComponent, T> {

        void bind(W group, UIAdapter<StackLayout> adapter, T machine);
    }
}
