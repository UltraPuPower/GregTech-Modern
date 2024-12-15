package com.gregtechceu.gtceu.integration.emi.oreprocessing;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.integration.emi.handler.UIEMIRecipe;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreByProductComponent;

import net.minecraft.resources.ResourceLocation;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import org.jetbrains.annotations.Nullable;

public class GTEmiOreProcessing extends UIEMIRecipe<GTOreByProductComponent> {

    final Material material;

    public GTEmiOreProcessing(Material material) {
        super(() -> new GTOreByProductComponent(material));
        this.material = material;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return GTOreProcessingEmiCategory.CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return material.getResourceLocation();
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
