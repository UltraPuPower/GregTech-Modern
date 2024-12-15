package com.gregtechceu.gtceu.integration.jei.recipe;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.gregtechceu.gtceu.integration.jei.handler.UIRecipeCategory;
import com.gregtechceu.gtceu.integration.xei.widgets.GTRecipeComponent;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.jei.IGui2IDrawable;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class GTRecipeJEICategory extends UIRecipeCategory<GTRecipeComponent> {

    public static final Function<GTRecipeCategory, RecipeType<GTRecipeComponent>> TYPES = Util
            .memoize(c -> new RecipeType<>(c.registryKey, GTRecipeComponent.class));

    private final GTRecipeCategory category;
    @Getter
    private final IDrawable background;
    @Getter
    private final IDrawable icon;

    public GTRecipeJEICategory(IJeiHelpers helpers,
                               @NotNull GTRecipeCategory category) {
        this.category = category;
        var recipeType = category.getRecipeType();
        IGuiHelper guiHelper = helpers.getGuiHelper();
        var size = recipeType.getRecipeUI().getRecipeViewerSize();
        this.background = guiHelper.createBlankDrawable(size.width(), size.height());
        this.icon = toDrawable(category.getIcon(), 16, 16);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        for (GTRecipeCategory category : GTRegistries.RECIPE_CATEGORIES) {
            if (!category.isXEIVisible() && !Platform.isDevEnv()) continue;
            var type = category.getRecipeType();
            if (category == type.getCategory()) type.buildRepresentativeRecipes();
            var wrapped = type.getRecipesInCategory(category).stream()
                    .map(GTRecipeComponent::new)
                    .toList();
            registration.addRecipes(TYPES.apply(category), wrapped);
        }
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (MachineDefinition machine : GTRegistries.MACHINES) {
            if (machine.getRecipeTypes() == null) continue;
            for (GTRecipeType type : machine.getRecipeTypes()) {
                if (type == null) continue;
                for (GTRecipeCategory category : type.getCategories()) {
                    if (!category.isXEIVisible() && !Platform.isDevEnv()) continue;
                    registration.addRecipeCatalyst(machine.asStack(), machineType(category));
                }
            }
        }
    }

    public static RecipeType<?> machineType(GTRecipeCategory category) {
        if (category == GTRecipeTypes.FURNACE_RECIPES.getCategory()) return RecipeTypes.SMELTING;
        return TYPES.apply(category);
    }

    @Override
    @NotNull
    public RecipeType<GTRecipeComponent> getRecipeType() {
        return TYPES.apply(category);
    }

    @Override
    @NotNull
    public Component getTitle() {
        return Component.translatable(category.getLanguageKey());
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(@NotNull GTRecipeComponent component) {
        return component.getRecipe().id;
    }

    static IDrawable toDrawable(UITexture guiTexture, final int width, final int height) {
        return new IDrawable() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public void draw(@Nonnull GuiGraphics graphics, int x, int y) {
                if (guiTexture == null) return;
                if (!(graphics instanceof UIGuiGraphics)) graphics = UIGuiGraphics.of(graphics);
                var uiGraphics = (UIGuiGraphics) graphics;
                guiTexture.draw(uiGraphics, 0, 0, x, y, width, height);
            }
        };
    }
}
