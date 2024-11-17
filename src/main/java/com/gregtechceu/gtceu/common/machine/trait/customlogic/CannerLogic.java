package com.gregtechceu.gtceu.common.machine.trait.customlogic;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.utils.GTStringUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CannerLogic implements GTRecipeType.ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemInputs = Objects
                .requireNonNullElseGet(holder.getCapabilitiesProxy().get(IO.IN, ItemRecipeCapability.CAP),
                        ArrayList::new)
                .stream()
                .filter(IItemHandlerModifiable.class::isInstance)
                .map(IItemHandlerModifiable.class::cast)
                .toArray(IItemHandlerModifiable[]::new);

        var fluidInputs = Objects
                .requireNonNullElseGet(holder.getCapabilitiesProxy().get(IO.IN, FluidRecipeCapability.CAP),
                        ArrayList::new)
                .stream()
                .filter(IFluidHandler.class::isInstance).map(IFluidHandler.class::cast)
                .toArray(IFluidHandler[]::new);

        var inputs = new CombinedInvWrapper(itemInputs);
        for (int i = 0; i < inputs.getSlots(); i++) {
            ItemStack item = inputs.getStackInSlot(i);
            if (!item.isEmpty()) {
                ItemStack inputStack = item.copy();
                inputStack.setCount(1);

                ItemStack fluidHandlerStack = inputStack.copy();
                IFluidHandlerItem fluidHandlerItem = fluidHandlerStack
                        .getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
                if (fluidHandlerItem == null)
                    continue;

                FluidStack fluid = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                if (!fluid.isEmpty()) {
                    return GTRecipeTypes.CANNER_RECIPES.recipeBuilder(GTStringUtils.itemStackToString(item))
                            .inputItems(inputStack)
                            .outputItems(fluidHandlerItem.getContainer())
                            .outputFluids(new FluidStack(fluid.getFluid(),
                                    fluid.getAmount(), fluid.getTag()))
                            .duration(Math.max(16, fluid.getAmount() / 64)).EUt(4)
                            .buildRawRecipe();
                }

                // nothing drained so try filling
                for (IFluidHandler fluidInput : fluidInputs) {
                    var fluidStack1 = fluidInput.getFluidInTank(0);
                    if (fluidStack1.isEmpty()) {
                        continue;
                    }
                    fluidStack1 = fluidStack1.copy();
                    fluidStack1.setAmount(
                            fluidHandlerItem.fill(new FluidStack(fluidStack1.getFluid(), fluidStack1.getAmount()),
                                    IFluidHandler.FluidAction.EXECUTE));
                    if (fluidStack1.getAmount() > 0) {
                        return GTRecipeTypes.CANNER_RECIPES.recipeBuilder(GTStringUtils.itemStackToString(item))
                                .inputItems(inputStack)
                                .inputFluids(fluidStack1)
                                .outputItems(fluidHandlerItem.getContainer())
                                .duration(Math.max(16, fluid.getAmount() / 64)).EUt(4)
                                .buildRawRecipe();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable List<GTRecipe> getRepresentativeRecipes() {
        ItemStack emptyCan = GTItems.FLUID_CELL_UNIVERSAL.asStack();
        emptyCan.setHoverName(Component.translatable("gtceu.empty_can.any_empty_can"));
        FluidStack water = new FluidStack(Fluids.WATER, 1000);
        ItemStack filledCan = GTItems.FLUID_CELL_UNIVERSAL.asStack();
        filledCan.setHoverName(Component.translatable("gtceu.empty_can.any_filled_can"));

        return Collections.singletonList(
                GTRecipeTypes.CANNER_RECIPES.recipeBuilder("fill_any_fluid_cell")
                        .inputItems(emptyCan)
                        .inputFluids(water)
                        .outputItems(filledCan)
                        .duration(16)
                        .EUt(4)
                        .category(GTRecipeCategories.DUMMY)
                        .buildRawRecipe());
    }
}
