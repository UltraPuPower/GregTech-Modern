package com.gregtechceu.gtceu.integration.jei.multipage;

import com.gregtechceu.gtceu.api.ui.component.PatternPreviewComponent;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;

public class MultiblockInfoWrapper extends ModularWrapper<PatternPreviewComponent> {

    public final MultiblockMachineDefinition definition;

    public MultiblockInfoWrapper(MultiblockMachineDefinition definition) {
        super(PatternPreviewComponent.getPatternWidget(definition));
        this.definition = definition;
    }
}
