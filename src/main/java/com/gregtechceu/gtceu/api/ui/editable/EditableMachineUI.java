package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.ui.container.ComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelLoader;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EditableMachineUI implements IEditableUI<ComponentGroup, MetaMachine> {

    @Getter
    final ResourceLocation uiPath;
    final Supplier<ComponentGroup> widgetSupplier;
    final BiConsumer<ComponentGroup, MetaMachine> binder;
    @Nullable
    private UIModel customUICache;

    public EditableMachineUI(ResourceLocation uiPath, Supplier<ComponentGroup> widgetSupplier,
                             BiConsumer<ComponentGroup, MetaMachine> binder) {
        this.uiPath = uiPath;
        this.widgetSupplier = widgetSupplier;
        this.binder = binder;
    }

    public ComponentGroup createDefault() {
        return widgetSupplier.get();
    }

    @Override
    public void setupUI(ComponentGroup template, MetaMachine machine) {
        binder.accept(template, machine);
    }

    //////////////////////////////////////
    // ******** GUI *********//
    //////////////////////////////////////

    @Nullable
    public ComponentGroup createCustomUI() {
        if (hasCustomUI()) {
            var model = getCustomUI();
            var group = model.parseComponentTree(ComponentGroup.class);
            group.moveTo(0, 0);
            return group;
        }
        return null;
    }

    public UIModel getCustomUI() {
        if (this.customUICache == null) {
            this.customUICache = UIModelLoader.get(uiPath);
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
