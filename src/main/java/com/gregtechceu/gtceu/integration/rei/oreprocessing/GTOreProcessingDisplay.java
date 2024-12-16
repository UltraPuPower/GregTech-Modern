package com.gregtechceu.gtceu.integration.rei.oreprocessing;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.integration.rei.handler.UIREIDisplay;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreByProductComponent;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class GTOreProcessingDisplay extends UIREIDisplay<GTOreByProductComponent> {

    private final Material material;

    public GTOreProcessingDisplay(Material material) {
        super(() -> new GTOreByProductComponent(material), GTOreProcessingDisplayCategory.CATEGORY);
        this.material = material;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(material.getResourceLocation());
    }
}
