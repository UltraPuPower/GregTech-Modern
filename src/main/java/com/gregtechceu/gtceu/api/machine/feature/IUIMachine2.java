package com.gregtechceu.gtceu.api.machine.feature;

import com.gregtechceu.gtceu.api.ui.UIContainer;
import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public interface IUIMachine2 extends MenuProvider, IMachineFeature {

    default boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    default InteractionResult tryToOpenUI(Player player, InteractionHand hand, BlockHitResult result) {
        if(this.shouldOpenUI(player, hand, result)) {
            if(player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, this);
            }
        } else {
            return InteractionResult.PASS;
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    default boolean isRemote() {
        var level = self().getLevel();
        return level == null ? LDLib.isRemote() : level.isClientSide;
    }

    @Nullable
    @Override
    default AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new UIContainer(i, inventory);
    }

    @Override
    default Component getDisplayName() {
        return Component.empty();
    }
}
