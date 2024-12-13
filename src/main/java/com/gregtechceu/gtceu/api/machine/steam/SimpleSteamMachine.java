package com.gregtechceu.gtceu.api.machine.steam;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IExhaustVentMachine;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.logic.OCParams;
import com.gregtechceu.gtceu.api.recipe.logic.OCResult;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.PredicatedTextureComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.util.SlotGenerator;
import com.gregtechceu.gtceu.common.recipe.condition.VentCondition;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidType;

import com.google.common.collect.Tables;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleSteamMachine extends SteamWorkableMachine implements IExhaustVentMachine, IUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SimpleSteamMachine.class,
            SteamWorkableMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    public final NotifiableItemStackHandler importItems;
    @Persisted
    public final NotifiableItemStackHandler exportItems;
    @Setter
    @Persisted
    private boolean needsVenting;

    public SimpleSteamMachine(IMachineBlockEntity holder, boolean isHighPressure, Object... args) {
        super(holder, isHighPressure, args);
        this.importItems = createImportItemHandler(args);
        this.exportItems = createExportItemHandler(args);
    }

    //////////////////////////////////////
    // ***** Initialization *****//
    //////////////////////////////////////

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected NotifiableFluidTank createSteamTank(Object... args) {
        return new NotifiableFluidTank(this, 1, 16 * FluidType.BUCKET_VOLUME, IO.IN);
    }

    protected NotifiableItemStackHandler createImportItemHandler(@SuppressWarnings("unused") Object... args) {
        return new NotifiableItemStackHandler(this, getRecipeType().getMaxInputs(ItemRecipeCapability.CAP), IO.IN);
    }

    protected NotifiableItemStackHandler createExportItemHandler(@SuppressWarnings("unused") Object... args) {
        return new NotifiableItemStackHandler(this, getRecipeType().getMaxOutputs(ItemRecipeCapability.CAP), IO.OUT);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Fine, we use it to provide eu cap for recipe, simulating an EU machine.
        capabilitiesProxy.put(IO.IN, EURecipeCapability.CAP,
                List.of(new SteamEnergyRecipeHandler(steamTank, 1d)));
    }

    @Override
    public void onMachineRemoved() {
        clearInventory(importItems.storage);
        clearInventory(exportItems.storage);
    }

    //////////////////////////////////////
    // ****** Venting Logic ******//
    //////////////////////////////////////

    @Override
    public float getVentingDamage() {
        return isHighPressure() ? 12F : 6F;
    }

    @Override
    public @NotNull Direction getVentingDirection() {
        return getOutputFacing();
    }

    @Override
    public boolean isNeedsVenting() {
        return this.needsVenting;
    }

    @Override
    public void markVentingComplete() {
        this.needsVenting = false;
    }

    //////////////////////////////////////
    // ****** Recipe Logic ******//
    //////////////////////////////////////

    @Nullable
    public static GTRecipe recipeModifier(MetaMachine machine, @NotNull GTRecipe recipe, @NotNull OCParams params,
                                          @NotNull OCResult result) {
        if (machine instanceof SimpleSteamMachine steamMachine) {
            if (RecipeHelper.getRecipeEUtTier(recipe) > GTValues.LV || !steamMachine.checkVenting()) {
                return null;
            }

            var modified = recipe.copy();
            modified.conditions.add(VentCondition.INSTANCE);

            if (steamMachine.isHighPressure) {
                result.init(RecipeHelper.getInputEUt(recipe) * 2L, modified.duration, params.getOcAmount());
            } else {
                result.init(RecipeHelper.getInputEUt(recipe), modified.duration * 2, params.getOcAmount());
            }

            return modified;
        }
        return null;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        needsVenting = true;
        checkVenting();
    }

    //////////////////////////////////////
    // *********** GUI ***********//
    //////////////////////////////////////

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        var progressProperty = menu.createProperty(double.class, "progress", recipeLogic.getProgressPercent());
        recipeLogic.addProgressPercentListener(progressProperty::set);

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

    @Override
    public void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter) {
        var menu = adapter.menu();
        var rootComponent = adapter.rootComponent;
        var screenGroup = UIContainers.group(Sizing.fixed(176), Sizing.fixed(166));
        screenGroup.padding(Insets.of(5));
        screenGroup.surface(isHighPressure ? Surface.UI_BACKGROUND_STEEL : Surface.UI_BACKGROUND_BRONZE);

        var storages = Tables.newCustomTable(new EnumMap<>(IO.class), LinkedHashMap<RecipeCapability<?>, Object>::new);
        storages.put(IO.IN, ItemRecipeCapability.CAP, importItems.storage);
        storages.put(IO.OUT, ItemRecipeCapability.CAP, exportItems.storage);

        //noinspection DataFlowIssue
        var group = getRecipeType().getRecipeUI().createUITemplate(menu.<Double>getProperty("progress")::get,
                adapter,
                storages,
                new CompoundTag(),
                Collections.emptyList(),
                true,
                isHighPressure);
        Positioning pos = Positioning.absolute((Math.max(group.width() + 4 + 8, 176) - 4 - group.width()) / 2 + 4,
                32);
        group.positioning(pos);
        screenGroup.child(group);

        screenGroup.child(UIComponents.label(getBlockState().getBlock().getName())
                .positioning(Positioning.relative(0, 0)));
        screenGroup.child(new PredicatedTextureComponent(GuiTextures.INDICATOR_NO_STEAM.get(isHighPressure), 18, 18)
                .positioning(Positioning.absolute(pos.x + group.width() / 2 - 9,
                        pos.y + group.height() / 2 - 9)))
                .child(UIComponents.playerInventory(player.getInventory(), GuiTextures.SLOT));

        rootComponent.child(screenGroup);
    }

}
