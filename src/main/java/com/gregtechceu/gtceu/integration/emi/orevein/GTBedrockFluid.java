package com.gregtechceu.gtceu.integration.emi.orevein;

import com.gregtechceu.gtceu.api.data.worldgen.bedrockfluid.BedrockFluidDefinition;
import com.gregtechceu.gtceu.client.ClientProxy;
import com.gregtechceu.gtceu.integration.emi.handler.UIEMIRecipe;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinComponent;

import net.minecraft.resources.ResourceLocation;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import org.jetbrains.annotations.Nullable;

public class GTBedrockFluid extends UIEMIRecipe<GTOreVeinComponent> {

    private final BedrockFluidDefinition fluid;

    public GTBedrockFluid(BedrockFluidDefinition fluid) {
        super(() -> new GTOreVeinComponent(fluid));
        this.fluid = fluid;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return GTBedrockFluidEmiCategory.CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return ClientProxy.CLIENT_FLUID_VEINS.inverse().get(fluid).withPrefix("/bedrock_fluid_diagram/");
    }
}
