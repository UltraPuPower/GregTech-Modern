package com.gregtechceu.gtceu.integration.emi.recipe;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.emi.IGui2Renderable;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;

import java.util.function.Function;
import java.util.stream.Stream;

public class GTRecipeEMICategory extends EmiRecipeCategory {

    public static final Function<GTRecipeCategory, GTRecipeEMICategory> CATEGORIES = Util
            .memoize(GTRecipeEMICategory::new);
    private final GTRecipeCategory category;

    private GTRecipeEMICategory(GTRecipeCategory category) {
        super(category.registryKey, IGui2Renderable.toDrawable(category.getIcon(), 16, 16));
        this.category = category;
    }

    public static void registerDisplays(EmiRegistry registry) {
        for (GTRecipeCategory category : GTRegistries.RECIPE_CATEGORIES) {
            if (!category.isXEIVisible() && !Platform.isDevEnv()) continue;
            var type = category.getRecipeType();
            EmiRecipeCategory emiCategory = CATEGORIES.apply(category);
            var recipes = type.getRecipesInCategory(category).stream();
            Stream.concat(recipes, type.getRepresentativeRecipes().stream())
                    .map(recipe -> new GTEmiRecipe(recipe, emiCategory))
                    .forEach(registry::addRecipe);
        }
    }

    public static void registerWorkStations(EmiRegistry registry) {
        for (MachineDefinition machine : GTRegistries.MACHINES) {
            if (machine.getRecipeTypes() == null) continue;
            for (GTRecipeType type : machine.getRecipeTypes()) {
                if (type == null) continue;
                for (GTRecipeCategory category : type.getCategories()) {
                    if (!category.isXEIVisible() && !Platform.isDevEnv()) continue;
                    registry.addWorkstation(machineCategory(category), EmiStack.of(machine.asStack()));
                }
            }
        }
    }

    public static EmiRecipeCategory machineCategory(GTRecipeCategory category) {
        if (category == GTRecipeTypes.FURNACE_RECIPES.getCategory()) return VanillaEmiRecipeCategories.SMELTING;
        else return CATEGORIES.apply(category);
    }

    @Override
    public Component getName() {
        return Component.translatable(category.getLanguageKey());
    }
}
