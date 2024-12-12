package com.gregtechceu.gtceu.api.ui;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

public class UIContainerScreen extends BaseContainerScreen<UIComponentGroup, UIContainerMenu<?>> {

    public UIContainerScreen(UIContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @SuppressWarnings({ "unchecked", "DataFlowIssue", "rawtypes" })
    @Override
    protected @NotNull UIAdapter<UIComponentGroup> createAdapter() {
        return ((UIContainerMenu) menu).getFactory().createAdapter(menu.player(), menu.getHolder());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void build(UIComponentGroup rootComponent) {
        ((UIContainerMenu) menu).getFactory().loadClientUI(menu.player(), this.uiAdapter, menu.getHolder());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (!invalid) {
            var updates = this.getMenu().getReceivedComponentUpdates();
            while (!updates.isEmpty()) {
                UIContainerMenu.IComponentUpdate update = updates.poll();
                if (update == null) {
                    continue;
                }
                this.uiAdapter.rootComponent.receiveMessage(update.updateId(), update.updateData());
            }
        }
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}
