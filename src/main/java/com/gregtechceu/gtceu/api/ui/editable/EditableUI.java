package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.util.UIComponentUtils;

import lombok.Getter;

import java.util.function.Supplier;

public class EditableUI<W extends UIComponent, T> implements IEditableUI<W, T> {

    @Getter
    final String id;
    final Class<W> clazz;
    @Getter
    final Supplier<W> componentSupplier;
    @Getter
    final BinderFunction<W, T> binder;

    public EditableUI(String id, Class<W> clazz, Supplier<W> componentSupplier, BinderFunction<W, T> binder) {
        this.id = id;
        this.clazz = clazz;
        this.componentSupplier = componentSupplier;
        this.binder = binder;
    }

    public W createDefault() {
        var component = componentSupplier.get();
        component.id(id);
        return component;
    }

    @Override
    public void setupUI(StackLayout template, UIAdapter<StackLayout> adapter, T instance) {
        UIComponentUtils.componentByIdForEach(template, "^" + id + "$", clazz, w -> binder.bind(w, adapter, instance));
    }
}
