package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.gui.UIComponentUtils;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EditableUI<W extends UIComponent, T> implements IEditableUI<W, T> {

    @Getter
    final String id;
    final Class<W> clazz;
    @Getter
    final Supplier<W> widgetSupplier;
    @Getter
    final BiConsumer<W, T> binder;

    public EditableUI(String id, Class<W> clazz, Supplier<W> widgetSupplier, BiConsumer<W, T> binder) {
        this.id = id;
        this.clazz = clazz;
        this.widgetSupplier = widgetSupplier;
        this.binder = binder;
    }

    public W createDefault() {
        var widget = widgetSupplier.get();
        widget.id(id);
        return widget;
    }

    public void setupUI(ParentUIComponent template, T instance) {
        UIComponentUtils.widgetByIdForEach(template, "^" + id + "$", clazz, w -> binder.accept(w, instance));
    }
}
