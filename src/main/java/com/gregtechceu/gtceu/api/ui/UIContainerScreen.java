package com.gregtechceu.gtceu.api.ui;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

public class UIContainerScreen extends BaseContainerScreen<RootContainer, UIContainer<?>> {

    public UIContainerScreen(UIContainer handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected @NotNull UIAdapter<RootContainer> createAdapter() {
        return menu.getAdapter();
    }

    // empty as the adapter is filled before this happens (in UIFactory)
    @Override
    protected void build(RootContainer rootComponent) {}
}
