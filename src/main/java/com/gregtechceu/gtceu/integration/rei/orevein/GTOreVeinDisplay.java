package com.gregtechceu.gtceu.integration.rei.orevein;

import com.gregtechceu.gtceu.api.data.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.integration.rei.handler.UIREIDisplay;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinComponent;

import net.minecraft.world.item.ItemStack;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.ArrayList;
import java.util.List;

public class GTOreVeinDisplay extends UIREIDisplay<GTOreVeinComponent> {

    private final GTOreDefinition oreDefinition;

    public GTOreVeinDisplay(GTOreDefinition oreDefinition) {
        super(() -> new GTOreVeinComponent(oreDefinition), GTOreVeinDisplayCategory.CATEGORY);
        this.oreDefinition = oreDefinition;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<EntryIngredient> ingredients = new ArrayList<>();
        for (ItemStack output : GTOreVeinComponent.getContainedOresAndBlocks(oreDefinition)) {
            ingredients.add(EntryIngredients.of(output));
        }
        return ingredients;
    }
}
