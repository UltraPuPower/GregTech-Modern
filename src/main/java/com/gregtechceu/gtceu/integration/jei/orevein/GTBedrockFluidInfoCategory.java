package com.gregtechceu.gtceu.integration.jei.orevein;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.client.ClientProxy;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.integration.jei.handler.UIRecipeCategory;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinComponent;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.jetbrains.annotations.NotNull;

public class GTBedrockFluidInfoCategory extends UIRecipeCategory<GTOreVeinComponent> {

    public final static RecipeType<GTOreVeinComponent> RECIPE_TYPE = new RecipeType<>(
            GTCEu.id("bedrock_fluid_diagram"), GTOreVeinComponent.class);
    private final IDrawable background;
    private final IDrawable icon;

    public GTBedrockFluidInfoCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createBlankDrawable(GTOreVeinComponent.width, 120);
        this.icon = helpers.getGuiHelper()
                .createDrawableItemStack(GTMaterials.Oil.getFluid().getBucket().asItem().getDefaultInstance());
    }

    public static void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RECIPE_TYPE, ClientProxy.CLIENT_FLUID_VEINS.values().stream()
                .map(GTOreVeinComponent::new)
                .toList());
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(GTItems.PROSPECTOR_HV.asStack(), RECIPE_TYPE);
        registration.addRecipeCatalyst(GTItems.PROSPECTOR_LuV.asStack(), RECIPE_TYPE);
    }

    @Override
    public void draw(GTOreVeinComponent recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public RecipeType<GTOreVeinComponent> getRecipeType() {
        return RECIPE_TYPE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.jei.bedrock_fluid_diagram");
    }

    @NotNull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @NotNull
    @Override
    public IDrawable getIcon() {
        return icon;
    }
}
