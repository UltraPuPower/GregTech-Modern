package com.gregtechceu.gtceu.api.recipe.ingredient;

import com.gregtechceu.gtceu.api.tag.TagUtil;
import com.gregtechceu.gtceu.data.tag.GTIngredientTypes;
import com.gregtechceu.gtceu.utils.InfiniteFluidTransfer;

import com.lowdragmc.lowdraglib.side.fluid.*;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

public class FluidContainerIngredient implements ICustomIngredient {

    public static final MapCodec<FluidContainerIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    SizedFluidIngredient.NESTED_CODEC.fieldOf("fluid").forGetter(FluidContainerIngredient::getFluid))
            .apply(instance, FluidContainerIngredient::new));

    @Getter
    private final SizedFluidIngredient fluid;

    public FluidContainerIngredient(SizedFluidIngredient fluid) {
        this.fluid = fluid;
    }

    public FluidContainerIngredient(FluidStack fluidStack) {
        this(SizedFluidIngredient.of(
                TagUtil.createFluidTag(BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).getPath()),
                fluidStack.getAmount()));
    }

    public FluidContainerIngredient(TagKey<Fluid> tag, int amount) {
        this(SizedFluidIngredient.of(tag, amount));
    }

    private Stream<ItemStack> cachedStacks;

    @Nonnull
    @Override
    public Stream<ItemStack> getItems() {
        if (cachedStacks == null)
            cachedStacks = Arrays.stream(this.fluid.getFluids())
                    .map(FluidUtil::getFilledBucket)
                    .filter(s -> !s.isEmpty())
                    .toArray(ItemStack[]::new);
        return this.cachedStacks;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        return FluidUtil.getFluidContained(stack).map(fluid::test).orElse(false) &&
                FluidUtil.tryEmptyContainer(stack, VoidFluidHandler.INSTANCE, fluid.getAmount(), null, false)
                        .isSuccess();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return GTIngredientTypes.FLUID_CONTAINER_INGREDIENT.get();
    }

    public ItemStack getExtractedStack(ItemStack input) {
        FluidActionResult result = FluidUtil.tryEmptyContainer(input, VoidFluidHandler.INSTANCE, fluid.getAmount(),
                ForgeHooks.getCraftingPlayer(), true);
        if (result.isSuccess()) {
            return result.getResult();
        }
        return input;
    }
}
