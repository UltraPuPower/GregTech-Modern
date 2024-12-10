package com.gregtechceu.gtceu.api.ui.holder;

import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;

import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import net.minecraft.world.entity.player.Player;

public interface IUIHolder<T> {

    IUIHolder<?> EMPTY = new IUIHolder<>() {

        @Override
        public void loadServerUI(Player player, UIContainerMenu<Object> menu, Object holder) {}

        @Override
        public void loadClientUI(Player player, UIAdapter<RootContainer> adapter) {}

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public boolean isClientSide() {
            return true;
        }

        @Override
        public void markDirty() {}
    };

    void loadServerUI(Player player, UIContainerMenu<T> menu, T holder);

    void loadClientUI(Player player, UIAdapter<RootContainer> adapter);

    boolean isInvalid();

    boolean isClientSide();

    void markDirty();
}
