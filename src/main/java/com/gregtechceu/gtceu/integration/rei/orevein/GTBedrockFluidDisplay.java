package com.gregtechceu.gtceu.integration.rei.orevein;

import com.gregtechceu.gtceu.api.data.worldgen.bedrockfluid.BedrockFluidDefinition;
import com.gregtechceu.gtceu.integration.rei.handler.UIREIDisplay;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinComponent;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.ArrayList;
import java.util.List;

public class GTBedrockFluidDisplay extends UIREIDisplay<GTOreVeinComponent> {

    private final BedrockFluidDefinition fluid;

    public GTBedrockFluidDisplay(BedrockFluidDefinition fluid) {
        super(() -> new GTOreVeinComponent(fluid), GTBedrockFluidDisplayCategory.CATEGORY);
        this.fluid = fluid;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<EntryIngredient> outputs = new ArrayList<>();
        outputs.add(EntryIngredients.of(fluid.getStoredFluid().get()));
        return outputs;
    }
}
