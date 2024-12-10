package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface AbstractContainerMenuAccessor {

    @Accessor("lastSlots")
    NonNullList<ItemStack> gtceu$getLastSlots();

    @Accessor("remoteSlots")
    NonNullList<ItemStack> gtceu$getRemoteSlots();

    @Invoker("addSlot")
    Slot gtceu$addSlot(Slot slot);
}
