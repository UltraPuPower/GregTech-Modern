package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.factory.HeldItemUIFactory;
import com.gregtechceu.gtceu.api.ui.holder.HeldItemUIHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IItemUIBehaviour extends IInteractionItem {

    @Override
    default InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        if(player instanceof ServerPlayer serverPlayer) {
            HeldItemUIFactory.INSTANCE.openUI(serverPlayer, usedHand);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    void loadUITemplate(Player entityPlayer, RootContainer rootComponent, HeldItemUIHolder holder);
}
