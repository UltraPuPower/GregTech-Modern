package com.gregtechceu.gtceu.integration.emi.recipe;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.GTRecipeWidget;

import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import com.lowdragmc.lowdraglib.emi.ModularForegroundRenderWidget;
import com.lowdragmc.lowdraglib.emi.ModularWrapperWidget;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TankWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GTEmiRecipe extends ModularEmiRecipe<WidgetGroup> {

    @Getter
    final EMIRecipeCategory category;
    final RecipeHolder<GTRecipe> recipe;

    public GTEmiRecipe(RecipeHolder<GTRecipe> recipe, EMIRecipeCategory category) {
        super(() -> new GTRecipeWidget(recipe));
        this.category = category;
        this.recipe = recipe;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return recipe.id();
    }
}
