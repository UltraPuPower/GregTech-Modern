package com.gregtechceu.gtceu.api.machine.feature;

import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public interface IUIContainer {

    IUIContainer EMPTY = new IUIContainer() {
        @Override
        public UIAdapter<?> createMenu(Player player) {
            return null;
        }

        @Override
        public boolean isInvalid() { return false; }

        @Override
        public boolean isClientSide() { return false; }

        @Override
        public void markDirty() {}
    };

    UIAdapter<?> createMenu(Player player);

    boolean isInvalid();

    boolean isClientSide();

    void markDirty();
}
