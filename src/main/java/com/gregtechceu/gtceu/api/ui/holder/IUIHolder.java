package com.gregtechceu.gtceu.api.ui.holder;

import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import net.minecraft.world.entity.player.Player;

public interface IUIHolder {

    IUIHolder EMPTY = new IUIHolder() {
        @Override
        public void loadUITemplate(Player entityPlayer, RootContainer rootComponent) {}

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public boolean isClientSide() {
            return true;
        }

        @Override
        public void markDirty() {

        }
    };

    void loadUITemplate(Player entityPlayer, RootContainer rootComponent);

    boolean isInvalid();

    boolean isClientSide();

    void markDirty();

}
