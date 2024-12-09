package com.gregtechceu.gtceu.api.ui;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

public class UIContainerScreen extends BaseContainerScreen<RootContainer, UIContainerMenu<?>> {

    public UIContainerScreen(UIContainerMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @SuppressWarnings({ "unchecked", "DataFlowIssue", "rawtypes" })
    @Override
    protected @NotNull UIAdapter<RootContainer> createAdapter() {
        return ((UIContainerMenu) menu).getFactory().createAdapter(menu.player(), menu.getHolder());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void build(RootContainer rootComponent) {
        ((UIContainerMenu) menu).getFactory().loadUITemplate(menu.player(), rootComponent, menu.getHolder());
        // re-init the menu once we're done loading the UI on the client.
        menu.setRootComponent(rootComponent);
        menu.init();
    }
}
