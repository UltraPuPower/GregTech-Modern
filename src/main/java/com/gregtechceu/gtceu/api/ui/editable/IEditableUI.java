package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import java.util.function.Supplier;


public interface IEditableUI<W extends UIComponent, T> {

    W createDefault();

    void setupUI(ParentUIComponent template, UIAdapter<UIComponentGroup> adapter, T instance);

    record Normal<A extends UIComponent, B>(Supplier<A> supplier, BinderFunction<ParentUIComponent, B> binder)
            implements IEditableUI<A, B> {

        @Override
        public A createDefault() {
            return supplier.get();
        }

        @Override
        public void setupUI(ParentUIComponent template, UIAdapter<UIComponentGroup> adapter, B instance) {
            binder.bind(template, adapter, instance);
        }
    }

    @FunctionalInterface
    interface BinderFunction<W extends UIComponent, T> {
        void bind(W group, UIAdapter<UIComponentGroup> adapter, T machine);
    }
}
