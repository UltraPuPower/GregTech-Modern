package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelLoader;

import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EditableMachineUI implements IEditableUI<StackLayout, MetaMachine> {

    @Getter
    final ResourceLocation uiPath;
    final Supplier<StackLayout> widgetSupplier;
    final BinderFunction<ParentUIComponent, MetaMachine> binder;
    @Nullable
    private UIModel customUICache;

    public EditableMachineUI(ResourceLocation uiPath, Supplier<StackLayout> widgetSupplier,
                             BinderFunction<ParentUIComponent, MetaMachine> binder) {
        this.uiPath = uiPath;
        this.widgetSupplier = widgetSupplier;
        this.binder = binder;
    }

    public StackLayout createDefault() {
        return widgetSupplier.get();
    }

    @Override
    public void setupUI(ParentUIComponent template, UIAdapter<StackLayout> adapter, MetaMachine machine) {
        binder.bind(template, adapter, machine);
    }

    //////////////////////////////////////
    // ******** GUI *********//
    //////////////////////////////////////

    @Nullable
    public StackLayout createCustomUI() {
        if (hasCustomUI()) {
            var model = getCustomUI();
            var group = model.parseComponentTree(StackLayout.class);
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
