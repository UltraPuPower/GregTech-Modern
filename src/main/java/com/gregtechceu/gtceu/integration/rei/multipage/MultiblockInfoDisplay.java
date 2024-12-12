package com.gregtechceu.gtceu.integration.rei.multipage;

import com.gregtechceu.gtceu.api.ui.component.PatternPreviewComponent;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.rei.ModularDisplay;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class MultiblockInfoDisplay extends ModularDisplay<WidgetGroup> {

    public final MultiblockMachineDefinition definition;

    public MultiblockInfoDisplay(MultiblockMachineDefinition definition) {
        super(() -> PatternPreviewComponent.getPatternWidget(definition), MultiblockInfoDisplayCategory.CATEGORY);
        this.definition = definition;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(definition.getId());
    }
}
