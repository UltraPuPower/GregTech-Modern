package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelLoader;

import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EditableMachineUI implements IEditableUI<UIComponentGroup, MetaMachine> {

    @Getter
    final ResourceLocation uiPath;
    final Supplier<UIComponentGroup> widgetSupplier;
    final BinderFunction<UIComponentGroup, MetaMachine> binder;
    @Nullable
    private UIModel customUICache;

    public EditableMachineUI(ResourceLocation uiPath, Supplier<UIComponentGroup> widgetSupplier,
                             BinderFunction<UIComponentGroup, MetaMachine> binder) {
        this.uiPath = uiPath;
        this.widgetSupplier = widgetSupplier;
        this.binder = binder;
    }

    public UIComponentGroup createDefault() {
        return widgetSupplier.get();
    }

    @Override
    public void setupUI(UIComponentGroup template, UIAdapter<UIComponentGroup> adapter, MetaMachine machine) {
        binder.bind(template, adapter, machine);
    }

    //////////////////////////////////////
    // ******** GUI *********//
    //////////////////////////////////////

    @Nullable
    public UIComponentGroup createCustomUI() {
        if (hasCustomUI()) {
            var model = getCustomUI();
            var group = model.parseComponentTree(UIComponentGroup.class);
            group.moveTo(0, 0);
            return group;
        }
        return null;
    }

    public UIModel getCustomUI() {
        if (this.customUICache == null) {
            this.customUICache = UIModelLoader.get(uiPath.withPrefix("machine/"));
        }
        return this.customUICache;
    }

    public boolean hasCustomUI() {
        return getCustomUI() != null;
    }

    public void reloadCustomUI() {
        this.customUICache = null;
    }
}
