package com.gregtechceu.gtceu.api.ui;

import com.google.common.base.Preconditions;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;

public abstract class UIFactory<T> {


    public boolean tryOpenUI(T holder, Player player) {
        if(!(player instanceof ServerPlayer))
            return false;
        UIAdapter<?> adapter = createAdapter(holder, player);
        if(adapter == null) return false;


        FriendlyByteBuf serializedHolder = new FriendlyByteBuf(Unpooled.buffer());


        /*NetworkHooks.openScreen((ServerPlayer)player, new SimpleMenuProvider((id, inv, p) -> ));*/
        return false;
    }

    protected abstract UIAdapter<?> createAdapter(T holder, Player player);
}
