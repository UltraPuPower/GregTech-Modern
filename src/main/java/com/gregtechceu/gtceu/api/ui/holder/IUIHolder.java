package com.gregtechceu.gtceu.api.ui.holder;

import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IUIHolder<T> {

    IUIHolder<?> EMPTY = new IUIHolder<>() {

        @Override
        public void loadServerUI(Player player, UIContainerMenu<Object> menu, Object holder) {}

        @Override
        public void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, Object holder) {}

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

    @OnlyIn(Dist.CLIENT)
    void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, T holder);

    boolean isInvalid();

    boolean isClientSide();

    void markDirty();
}
