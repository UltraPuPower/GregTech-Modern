package com.gregtechceu.gtceu.api.ui.editor;

import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;


public interface IEditableUI2<W extends UIComponent, T> {

    W createDefault();

    void setupUI(RootContainer template, T instance);

    record Normal<A extends UIComponent, B>(Supplier<A> supplier, BiConsumer<RootContainer, B> binder)
            implements IEditableUI2<A, B> {

        @Override
        public A createDefault() {
            return supplier.get();
        }

        @Override
        public void setupUI(RootContainer template, B instance) {
            binder.accept(template, instance);
        }
    }
}
