package com.gregtechceu.gtceu.integration.rei.circuit;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.integration.rei.handler.UIDisplayCategory;
import com.gregtechceu.gtceu.integration.rei.handler.UIREIDisplay;
import com.gregtechceu.gtceu.integration.xei.widgets.GTProgrammedCircuitComponent;

import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.rei.IGui2Renderer;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

import java.util.Optional;

public class GTProgrammedCircuitCategory extends
                                         UIDisplayCategory<GTProgrammedCircuitCategory.GTProgrammedCircuitDisplay> {

    public static CategoryIdentifier<GTProgrammedCircuitDisplay> CATEGORY = CategoryIdentifier
            .of(GTCEu.id("programmed_circuit"));

    @Getter
    private final Renderer icon;
    @Getter
    private final Size size;

    public GTProgrammedCircuitCategory() {
        this.icon = IGui2Renderer.toDrawable(new ItemStackTexture(GTItems.PROGRAMMED_CIRCUIT.asItem()));
        this.size = new Size(158, 80);
    }

    @Override
    public CategoryIdentifier<? extends GTProgrammedCircuitDisplay> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.jei.programmed_circuit");
    }

    @Override
    public int getDisplayHeight() {
        return getSize().height;
    }

    @Override
    public int getDisplayWidth(GTProgrammedCircuitDisplay display) {
        return getSize().width;
    }

    public static class GTProgrammedCircuitDisplay extends UIREIDisplay<GTProgrammedCircuitComponent> {

        public GTProgrammedCircuitDisplay() {
            super(GTProgrammedCircuitComponent::new, GTProgrammedCircuitCategory.CATEGORY);
        }

        @Override
        public Optional<ResourceLocation> getDisplayLocation() {
            return Optional.of(GTCEu.id("programmed_circuit"));
        }
    }
}
