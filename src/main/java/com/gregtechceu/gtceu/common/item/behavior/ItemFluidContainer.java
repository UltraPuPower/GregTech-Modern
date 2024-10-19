package com.gregtechceu.gtceu.common.item.behavior;

import com.gregtechceu.gtceu.api.item.component.IRecipeRemainder;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Optional;

/**
 * @author KilaBash
 * @date 2023/2/23
 * @implNote ItemFluidContainer
 */
public class ItemFluidContainer implements IRecipeRemainder {

    @Override
    public ItemStack getRecipeRemained(ItemStack itemStack) {
        return Optional.ofNullable(FluidUtil.getFluidHandler(itemStack)).map(handler -> {
            var drained = handler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
            if (drained.getAmount() != FluidType.BUCKET_VOLUME) return ItemStack.EMPTY;
            handler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
            var copy = handler.getContainer();
            copy.setTag(null);
            return copy;
        }).orElse(itemStack);
    }
}
