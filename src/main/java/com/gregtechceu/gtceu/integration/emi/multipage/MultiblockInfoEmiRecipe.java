package com.gregtechceu.gtceu.integration.emi.multipage;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.ui.component.PatternPreviewComponent;
import com.gregtechceu.gtceu.integration.emi.handler.UIEMIRecipe;

import net.minecraft.resources.ResourceLocation;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import org.jetbrains.annotations.Nullable;

public class MultiblockInfoEmiRecipe extends UIEMIRecipe<PatternPreviewComponent> {

    public final MultiblockMachineDefinition definition;

    public MultiblockInfoEmiRecipe(MultiblockMachineDefinition definition) {
        super(() -> PatternPreviewComponent.getPatternWidget(definition));
        this.definition = definition;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MultiblockInfoEmiCategory.CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return definition.getId();
    }
}
