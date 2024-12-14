package com.gregtechceu.gtceu.integration.emi.recipe;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.emi.UIEMIRecipe;
import com.gregtechceu.gtceu.integration.xei.widgets.GTRecipeComponent;

import net.minecraft.resources.ResourceLocation;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import org.jetbrains.annotations.Nullable;

public class GTEMIRecipe extends UIEMIRecipe<GTRecipeComponent> {

    final EmiRecipeCategory category;
    final GTRecipe recipe;

    public GTEMIRecipe(GTRecipe recipe, EmiRecipeCategory category) {
        super(() -> new GTRecipeComponent(recipe));
        this.category = category;
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return recipe.getId();
    }
}
