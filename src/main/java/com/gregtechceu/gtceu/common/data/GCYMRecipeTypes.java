package com.gregtechceu.gtceu.common.data;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.transfer.item.CycleItemStackHandler;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.core.Insets;
import com.gregtechceu.gtceu.api.ui.core.Positioning;

import com.gregtechceu.gtceu.utils.FormattingUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.ui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MULTIBLOCK;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.register;

/**
 * @author Rundas
 * @implNote Gregicality Multiblocks Recipe Types
 */
public class GCYMRecipeTypes {

    //////////////////////////////////////
    // ******* Multiblock *******//
    /// ///////////////////////////////////
    public final static GTRecipeType ALLOY_BLAST_RECIPES = register("alloy_blast_smelter", MULTIBLOCK)
            .setMaxIOSize(9, 0, 3, 1)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
            .setSlotOverlay(false, false, true, GuiTextures.FURNACE_OVERLAY_1)
            .setSlotOverlay(false, true, false, GuiTextures.FURNACE_OVERLAY_2)
            .setSlotOverlay(false, true, true, GuiTextures.FURNACE_OVERLAY_2)
            .setSlotOverlay(true, true, false, GuiTextures.FURNACE_OVERLAY_2)
            .setSlotOverlay(true, true, true, GuiTextures.FURNACE_OVERLAY_2)
            .addDataInfo(data -> {
                int temp = data.getInt("ebf_temp");
                return Component.translatable("gtceu.recipe.temperature", FormattingUtil.formatNumbers(temp));
            })
            .addDataInfo(data -> {
                int temp = data.getInt("ebf_temp");
                ICoilType requiredCoil = ICoilType.getMinRequiredType(temp);

                if (requiredCoil != null && requiredCoil.getMaterial() != null) {
                    return Component.translatable("gtceu.recipe.coil.tier",
                            I18n.get(requiredCoil.getMaterial().getUnlocalizedName()));
                }
                return Component.empty();
            })
            .setMaxTooltips(4)
            .setUiBuilder((recipe, widgetGroup) -> {
                int temp = recipe.data.getInt("ebf_temp");
                List<List<ItemStack>> items = new ArrayList<>();
                items.add(GTCEuAPI.HEATING_COILS.entrySet().stream()
                        .filter(coil -> coil.getKey().getCoilTemperature() >= temp)
                        .map(coil -> new ItemStack(coil.getValue().get())).toList());
                widgetGroup.child(UIComponents.slot(new CycleItemStackHandler(items), 0)
                        .canInsertOverride(false)
                        .canExtractOverride(false)
                        .positioning(Positioning.relative(100, 100))
                        .margins(Insets.of(0, 40, 0, 25)));
            })
            .setSound(GTSoundEntries.ARC);

    public static void init() {}
}
