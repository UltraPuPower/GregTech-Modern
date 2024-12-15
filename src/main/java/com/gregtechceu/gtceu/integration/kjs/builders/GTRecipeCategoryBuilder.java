package com.gregtechceu.gtceu.integration.kjs.builders;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.registrate.BuilderBase;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GTRecipeCategoryBuilder extends BuilderBase<GTRecipeCategory> {

    public transient String name;
    public transient GTRecipeType recipeType;
    private transient UITexture icon;
    private transient boolean isXEIVisible;

    public GTRecipeCategoryBuilder(ResourceLocation id, Object... args) {
        super(id);
        name = id.getPath();
        recipeType = null;
        icon = null;
        isXEIVisible = true;
    }

    public GTRecipeCategoryBuilder recipeType(GTRecipeType recipeType) {
        this.recipeType = recipeType;
        return this;
    }

    public GTRecipeCategoryBuilder setIcon(UITexture guiTexture) {
        this.icon = guiTexture;
        return this;
    }

    public GTRecipeCategoryBuilder setCustomIcon(ResourceLocation location) {
        this.icon = UITextures.resource(location.withPrefix("textures/").withSuffix(".png"));
        return this;
    }

    public GTRecipeCategoryBuilder setItemIcon(ItemStack stack) {
        this.icon = UITextures.item(stack);
        return this;
    }

    public GTRecipeCategoryBuilder isXEIVisible(boolean flag) {
        this.isXEIVisible = flag;
        return this;
    }

    @Override
    public GTRecipeCategory register() {
        var category = GTRecipeCategories.register(name, recipeType)
                .setIcon(icon)
                .setXEIVisible(isXEIVisible);
        return value = category;
    }
}
