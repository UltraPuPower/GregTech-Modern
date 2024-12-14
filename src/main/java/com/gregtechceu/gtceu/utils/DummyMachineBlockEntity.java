package com.gregtechceu.gtceu.utils;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import com.lowdragmc.lowdraglib.syncdata.managed.MultiManagedStorage;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Dummy machine BE used for wrapping {@link DummyRecipeLogicMachine}s
 */
public class DummyMachineBlockEntity implements IMachineBlockEntity {

    @Getter
    public final DummyRecipeLogicMachine metaMachine;
    @Getter
    private final MachineDefinition definition;

    public DummyMachineBlockEntity(int tier, GTRecipeType type, Int2IntFunction tankScalingFunction,
                                   Map<IO, List<RecipeHandlerList>> capabilitiesProxy,
                                   Object... args) {
        this.definition = MachineDefinition.createDefinition(GTCEu.id("dummy"));
        this.definition.setRecipeTypes(new GTRecipeType[] { type });
        this.definition.setTier(tier);

        this.metaMachine = new DummyRecipeLogicMachine(this, tier, tankScalingFunction, capabilitiesProxy, args);
    }

    @Override
    public long getOffset() {
        return 0;
    }

    @Override
    public MultiManagedStorage getRootStorage() {
        return null;
    }
}
