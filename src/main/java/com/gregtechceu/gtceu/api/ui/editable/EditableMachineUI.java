package com.gregtechceu.gtceu.api.ui.editable;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelLoader;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EditableMachineUI implements IEditableUI<RootContainer, MetaMachine> {

    @Getter
    final ResourceLocation uiPath;
    final Supplier<RootContainer> widgetSupplier;
    final BiConsumer<RootContainer, MetaMachine> binder;
    @Nullable
    private UIModel customUICache;

    public EditableMachineUI(ResourceLocation uiPath, Supplier<RootContainer> widgetSupplier,
                             BiConsumer<RootContainer, MetaMachine> binder) {
        this.uiPath = uiPath;
        this.widgetSupplier = widgetSupplier;
        this.binder = binder;
    }

    public RootContainer createDefault() {
        return widgetSupplier.get();
    }

    public void setupUI(RootContainer template, MetaMachine machine) {
        binder.accept(template, machine);
    }

    //////////////////////////////////////
    // ******** GUI *********//
    //////////////////////////////////////

    @Nullable
    public RootContainer createCustomUI() {
        if (hasCustomUI()) {
            var model = getCustomUI();
            var group = model.parseComponentTree(RootContainer.class);
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
