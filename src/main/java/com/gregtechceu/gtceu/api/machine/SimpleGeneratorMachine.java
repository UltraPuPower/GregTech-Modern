package com.gregtechceu.gtceu.api.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.editable.EditableMachineUI;
import com.gregtechceu.gtceu.api.machine.feature.IEnvironmentalHazardEmitter;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.logic.OCParams;
import com.gregtechceu.gtceu.api.recipe.logic.OCResult;
import com.gregtechceu.gtceu.api.recipe.ui.GTRecipeTypeUI;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.serialization.SyncedProperty;
import com.gregtechceu.gtceu.api.ui.util.SlotGenerator;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import com.google.common.collect.Tables;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/3/17
 * @implNote SimpleGeneratorMachine
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleGeneratorMachine extends WorkableTieredMachine
                                    implements IFancyUIMachine, IEnvironmentalHazardEmitter {

    @Getter
    private final float hazardStrengthPerOperation;

    public SimpleGeneratorMachine(IMachineBlockEntity holder, int tier,
                                  float hazardStrengthPerOperation, Int2IntFunction tankScalingFunction,
                                  Object... args) {
        super(holder, tier, tankScalingFunction, args);
        this.hazardStrengthPerOperation = hazardStrengthPerOperation;
    }

    public SimpleGeneratorMachine(IMachineBlockEntity holder, int tier, Int2IntFunction tankScalingFunction,
                                  Object... args) {
        this(holder, tier, 0.25f, tankScalingFunction, args);
    }
    //////////////////////////////////////
    // ***** Initialization ******//
    //////////////////////////////////////

    @Override
    protected NotifiableEnergyContainer createEnergyContainer(Object... args) {
        var energyContainer = super.createEnergyContainer(args);
        energyContainer.setSideOutputCondition(side -> !hasFrontFacing() || side == getFrontFacing());
        return energyContainer;
    }

    @Override
    protected boolean isEnergyEmitter() {
        return true;
    }

    @Override
    protected long getMaxInputOutputAmperage() {
        return 1L;
    }

    @Override
    public int tintColor(int index) {
        if (index == 2) {
            return GTValues.VC[getTier()];
        }
        return super.tintColor(index);
    }

    //////////////////////////////////////
    // ****** RECIPE LOGIC *******//
    //////////////////////////////////////

    @Nullable
    public static GTRecipe recipeModifier(MetaMachine machine, @NotNull GTRecipe recipe, @NotNull OCParams params,
                                          @NotNull OCResult result) {
        if (machine instanceof SimpleGeneratorMachine generator) {
            var EUt = RecipeHelper.getOutputEUt(recipe);
            if (EUt > 0) {
                var maxParallel = GTMath.saturatedCast(Math.min(generator.getOverclockVoltage(),
                        GTValues.V[generator.getOverclockTier()]) / EUt);
                var paraRecipe = GTRecipeModifiers.fastParallel(generator, recipe, maxParallel, false);
                result.init(-RecipeHelper.getOutputEUt(paraRecipe.getFirst()), paraRecipe.getFirst().duration,
                        paraRecipe.getSecond(), params.getOcAmount());
                return paraRecipe.getFirst();
            }
        }
        return null;
    }

    @Override
    public boolean dampingWhenWaiting() {
        return false;
    }

    @Override
    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        return capability != EURecipeCapability.CAP;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        spreadEnvironmentalHazard();
    }

    //////////////////////////////////////
    // *********** GUI ***********//
    //////////////////////////////////////

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        var recipeTypeProperty = menu.createProperty(int.class, "current_recipe_type", this.getActiveRecipeType());
        this.setRecipeTypeChangeListener(recipeTypeProperty::set);

        for (int i = 0; i < this.importFluids.getTanks(); i++) {
            SyncedProperty<FluidStack> prop = menu.createProperty(FluidStack.class, "fluid-in." + i,
                    this.importFluids.getFluidInTank(i));
            CustomFluidTank tank = this.importFluids.getStorages()[i];
            tank.addOnContentsChanged(() -> prop.set(tank.getFluid()));
        }
        for (int i = 0; i < this.exportFluids.getTanks(); i++) {
            SyncedProperty<FluidStack> prop = menu.createProperty(FluidStack.class, "fluid-out." + i,
                    this.exportFluids.getFluidInTank(i));
            CustomFluidTank tank = this.exportFluids.getStorages()[i];
            tank.addOnContentsChanged(() -> prop.set(tank.getFluid()));
        }
        // Position all slots at 0,0 as they'll be moved to the correct position on the client.
        SlotGenerator generator = SlotGenerator.begin(menu::addSlot, 0, 0);
        for (int i = 0; i < this.importItems.getSlots(); i++) {
            generator.slot(this.importItems, i, 0, 0);
        }
        for (int i = 0; i < this.exportItems.getSlots(); i++) {
            generator.slot(this.exportItems, i, 0, 0);
        }
        generator.playerInventory(menu.getPlayerInventory());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static BiFunction<ResourceLocation, GTRecipeType, EditableMachineUI> EDITABLE_UI_CREATOR = Util
            .memoize((path, recipeType) -> new EditableMachineUI(path, () -> {
                UIComponentGroup template = recipeType.getRecipeUI().createEditableUITemplate(false, false).createDefault();
                UIComponentGroup group = UIContainers.group(Sizing.content(),
                        Sizing.fixed(Math.max(template.height(), 78)));
                template.positioning(Positioning.relative(50, 50));
                group.child(template);
                return group;
            }, (template, adapter, machine) -> {
                if (machine instanceof SimpleGeneratorMachine generatorMachine) {
                    var storages = Tables.newCustomTable(new EnumMap<>(IO.class),
                            LinkedHashMap<RecipeCapability<?>, Object>::new);
                    storages.put(IO.IN, ItemRecipeCapability.CAP, generatorMachine.importItems.storage);
                    storages.put(IO.OUT, ItemRecipeCapability.CAP, generatorMachine.exportItems.storage);
                    storages.put(IO.IN, FluidRecipeCapability.CAP, generatorMachine.importFluids);
                    storages.put(IO.OUT, FluidRecipeCapability.CAP, generatorMachine.exportFluids);

                    generatorMachine.getRecipeType().getRecipeUI().createEditableUITemplate(false, false).setupUI(
                            template, adapter,
                            new GTRecipeTypeUI.RecipeHolder(generatorMachine.recipeLogic::getProgressPercent,
                                    storages,
                                    new CompoundTag(),
                                    Collections.emptyList(),
                                    false, false));
                    createEnergyBar().setupUI(template, adapter, generatorMachine);
                }
            }));
}
