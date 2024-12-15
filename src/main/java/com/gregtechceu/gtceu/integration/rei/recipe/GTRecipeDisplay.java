package com.gregtechceu.gtceu.integration.rei.recipe;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.rei.handler.UIREIDisplay;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinComponent;
import com.gregtechceu.gtceu.integration.xei.widgets.GTRecipeComponent;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.rei.ModularDisplay;

import net.minecraft.resources.ResourceLocation;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GTRecipeDisplay extends UIREIDisplay<GTRecipeComponent> {

    private final GTRecipe recipe;
    protected List<EntryIngredient> allInputs;

    public GTRecipeDisplay(GTRecipe recipe, CategoryIdentifier<?> category) {
        super(() -> new GTRecipeComponent(recipe), category);
        this.recipe = recipe;
        allInputs = new ArrayList<>(this.inputEntries);
        allInputs.addAll(catalysts);
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(recipe.id);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return allInputs;
    }

    @Override
    public List<EntryIngredient> getRequiredEntries() {
        return inputEntries;
    }
}
