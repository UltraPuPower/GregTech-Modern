package com.gregtechceu.gtceu.api.ui;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;

import net.minecraft.client.gui.GuiGraphics;
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
        ((UIContainerMenu) menu).getFactory().loadClientUI(menu.player(), this.uiAdapter, menu.getHolder());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (!invalid) {
            var updates = this.getMenu().getReceivedComponentUpdates();
            while (!updates.isEmpty()) {
                UIContainerMenu.ComponentUpdate update = updates.poll();
                if (update == null) {
                    continue;
                }
                this.uiAdapter.rootComponent.receiveMessage(update.updateId(), update.updateData());
            }
        }
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}
