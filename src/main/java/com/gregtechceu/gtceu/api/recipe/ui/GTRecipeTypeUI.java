package com.gregtechceu.gtceu.api.recipe.ui;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.SteamTexture;
import com.gregtechceu.gtceu.api.gui.UIComponentUtils;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.ui.component.ButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.DualProgressComponent;
import com.gregtechceu.gtceu.api.ui.component.ProgressComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.*;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.editable.IEditableUI;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelLoader;
import com.gregtechceu.gtceu.api.ui.texture.ProgressTexture;
import com.gregtechceu.gtceu.api.ui.texture.ResourceTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.integration.emi.recipe.GTRecipeEMICategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;
import com.gregtechceu.gtceu.integration.rei.recipe.GTRecipeREICategory;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.jei.JEIPlugin;

import net.minecraft.nbt.CompoundTag;

import com.google.common.collect.Table;
import dev.emi.emi.api.EmiApi;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class GTRecipeTypeUI {

    @Getter
    @Setter
    private Byte2ObjectMap<UITexture> slotOverlays = new Byte2ObjectArrayMap<>();

    private final GTRecipeType recipeType;

    @Getter
    @Setter
    private ProgressTexture progressBarTexture = UITextures.progress(
            GuiTextures.PROGRESS_BAR_ARROW.getSubTexture(0, 0, 1, 0.5),
            GuiTextures.PROGRESS_BAR_ARROW.getSubTexture(0, 0.5, 1, 0.5));
    @Setter
    private SteamTexture steamProgressBarTexture = null;
    @Setter
    private ProgressTexture.FillDirection steamMoveType = ProgressTexture.FillDirection.LEFT_TO_RIGHT;
    @Setter
    @Nullable
    protected BiConsumer<GTRecipe, UIComponentGroup> uiBuilder;
    @Setter
    @Getter
    protected int maxTooltips = 3;

    @Nullable
    private UIModel customUICache;
    private Size xeiSize;
    @Getter
    private int originalWidth;

    /**
     * @param recipeType the recipemap corresponding to this ui
     */
    public GTRecipeTypeUI(@NotNull GTRecipeType recipeType) {
        this.recipeType = recipeType;
    }

    public UIModel getCustomUI() {
        if (this.customUICache == null) {
            this.customUICache = UIModelLoader.get(recipeType.registryName.withPrefix("recipe_type/"));
        }
        return this.customUICache;
    }

    public boolean hasCustomUI() {
        return getCustomUI() != null;
    }

    public void reloadCustomUI() {
        this.customUICache = null;
        this.xeiSize = null;
    }

    public Size getJEISize() {
        Size size = this.xeiSize;
        if (size == null) {
            var originalSize = createEditableUITemplate(false, false).createDefault().fullSize();
            this.originalWidth = originalSize.width();
            this.xeiSize = size = Size.of(Math.max(originalWidth, 150),
                    getPropertyHeightShift() + 5 + originalSize.height());
        }
        return size;
    }

    public record RecipeHolder(DoubleSupplier progressSupplier,
                               Table<IO, RecipeCapability<?>, Object> storages,
                               CompoundTag data,
                               List<RecipeCondition> conditions,
                               boolean isSteam,
                               boolean isHighPressure) {

    }

    /**
     * Auto layout UI template for recipes.
     *
     * @param progressSupplier progress. To create a JEI / REI UI, use the para {@link ProgressComponent#JEIProgress}.
     */
    @OnlyIn(Dist.CLIENT)
    public UIComponentGroup createUITemplate(DoubleSupplier progressSupplier,
                                             UIAdapter<UIComponentGroup> adapter,
                                             Table<IO, RecipeCapability<?>, Object> storages,
                                             CompoundTag data,
                                             List<RecipeCondition> conditions,
                                             boolean isSteam,
                                             boolean isHighPressure) {
        var template = createEditableUITemplate(isSteam, isHighPressure);
        var group = template.createDefault();
        template.setupUI(group, adapter,
                new RecipeHolder(progressSupplier, storages, data, conditions, isSteam, isHighPressure));
        return group;
    }

    public UIComponentGroup createUITemplate(DoubleSupplier progressSupplier,
                                             UIAdapter<UIComponentGroup> adapter,
                                             Table<IO, RecipeCapability<?>, Object> storages,
                                             CompoundTag data,
                                             List<RecipeCondition> conditions) {
        return createUITemplate(progressSupplier, adapter, storages, data, conditions, false, false);
    }

    /**
     * Auto layout UI template for recipes.
     */
    public IEditableUI<UIComponentGroup, RecipeHolder> createEditableUITemplate(final boolean isSteam,
                                                                                final boolean isHighPressure) {
        return new IEditableUI.Normal<>(() -> {
            var isCustomUI = !isSteam && hasCustomUI();
            if (isCustomUI) {
                UIModel model = getCustomUI();
                UIComponentGroup group = model.parseComponentTree(UIComponentGroup.class);
                group.positioning(Positioning.absolute(0, 0));
                return group;
            }

            var inputs = addInventorySlotGroup(false, isSteam, isHighPressure);
            var outputs = addInventorySlotGroup(true, isSteam, isHighPressure);
            var maxWidth = Math.max(inputs.width(), outputs.width());
            var group = UIContainers.group(Sizing.fixed(2 * maxWidth + 40),
                    Sizing.fixed(Math.max(inputs.height(), outputs.height())));
            var size = group.fullSize();

            inputs.positioning(Positioning.relative(50, 50));
            outputs.positioning(Positioning.relative(50, 50));
            group.child(inputs);
            group.child(outputs);

            var progressWidget = UIComponents.progress(ProgressComponent.JEIProgress);
            progressWidget.progressTexture(progressBarTexture)
                    .positioning(Positioning.absolute(maxWidth + 10, size.height() / 2 - 10))
                    .sizing(Sizing.fixed(20));
            progressWidget.id("progress");
            group.child(progressWidget);

            progressWidget.progressTexture((isSteam && steamProgressBarTexture != null) ? UITextures.progress(
                            steamProgressBarTexture.get(isHighPressure).getSubTexture(0, 0, 1, 0.5),
                            steamProgressBarTexture.get(isHighPressure).getSubTexture(0, 0.5, 1, 0.5))
                    .fillDirection(steamMoveType) : progressBarTexture);

            return group;
        }, (template, adapter, recipeHolder) -> {
            var isJEI = recipeHolder.progressSupplier == ProgressComponent.JEIProgress;

            // bind progress
            List<UIComponent> progress = new ArrayList<>();
            // First set the progress suppliers separately.
            UIComponentUtils.componentByIdForEach(template, "^progress$", ProgressComponent.class, progressComponent -> {
                progressComponent.progressSupplier(recipeHolder.progressSupplier);
                progress.add(progressComponent);
            });
            // Then set the dual-progress widgets, to override their builtin ones' suppliers, in case someone forgot to
            // remove the id from the internal ones.
            UIComponentUtils.componentByIdForEach(template, "^progress$", DualProgressComponent.class, dualProgressComponent -> {
                dualProgressComponent.progressSupplier(recipeHolder.progressSupplier);
                progress.add(dualProgressComponent);
            });
            // add recipe button
            if (!isJEI && (LDLib.isReiLoaded() || LDLib.isJeiLoaded() || LDLib.isEmiLoaded())) {
                for (UIComponent component : progress) {
                    template.child(UIComponents.button(Component.empty(), cd -> {
                                if (LDLib.isReiLoaded()) {
                                    ViewSearchBuilder.builder().addCategories(
                                                    recipeType.getCategories().stream()
                                                            .filter(GTRecipeCategory::isXEIVisible)
                                                            .map(GTRecipeREICategory::machineCategory)
                                                            .collect(Collectors.toList()))
                                            .open();
                                } else if (LDLib.isJeiLoaded()) {
                                    JEIPlugin.jeiRuntime.getRecipesGui().showTypes(
                                            recipeType.getCategories().stream()
                                                    .filter(GTRecipeCategory::isXEIVisible)
                                                    .map(GTRecipeJEICategory::machineType)
                                                    .collect(Collectors.toList()));
                                } else if (LDLib.isEmiLoaded()) {
                                    EmiApi.displayRecipeCategory(
                                            GTRecipeEMICategory.machineCategory(recipeType.getCategory()));
                                }
                            }).renderer(ButtonComponent.Renderer.EMPTY)
                            .positioning(component.positioning().get())
                            .sizing(component.horizontalSizing().get(), component.verticalSizing().get())
                            .tooltip(List.of(Component.translatable("gtceu.recipe_type.show_recipes"))));
                }
            }

            // Bind I/O
            for (var capabilityEntry : recipeHolder.storages.rowMap().entrySet()) {
                IO io = capabilityEntry.getKey();
                for (var storagesEntry : capabilityEntry.getValue().entrySet()) {
                    RecipeCapability<?> cap = storagesEntry.getKey();
                    Object storage = storagesEntry.getValue();
                    // bind overlays
                    Class<? extends UIComponent> widgetClass = cap.getWidgetClass();
                    if (widgetClass != null) {
                        UIComponentUtils.componentByIdForEach(template, "^%s.[0-9]+$".formatted(cap.slotName(io)), widgetClass,
                                widget -> {
                                    var index = UIComponentUtils.componentIdIndex(widget);
                                    cap.applyWidgetInfo(widget, index, isJEI, io, recipeHolder, recipeType, null, null,
                                            storage, 0, 0);
                                });
                    }
                }
            }
        });
    }

    protected GridLayout addInventorySlotGroup(boolean isOutputs, boolean isSteam, boolean isHighPressure) {
        int maxCount = 0;
        int totalR = 0;
        TreeMap<RecipeCapability<?>, Integer> map = new TreeMap<>(RecipeCapability.COMPARATOR);
        if (isOutputs) {
            for (var value : recipeType.maxOutputs.entrySet()) {
                if (value.getKey().doRenderSlot) {
                    int val = value.getValue();
                    if (val > maxCount) {
                        maxCount = Math.min(val, 3);
                    }
                    totalR += (val + 2) / 3;
                    map.put(value.getKey(), val);
                }
            }
        } else {
            for (var value : recipeType.maxInputs.entrySet()) {
                if (value.getKey().doRenderSlot) {
                    int val = value.getValue();
                    if (val > maxCount) {
                        maxCount = Math.min(val, 3);
                    }
                    totalR += (val + 2) / 3;
                    map.put(value.getKey(), val);
                }
            }
        }
        GridLayout group = UIContainers.grid(Sizing.fixed(maxCount * 18 + 8), Sizing.fixed(totalR * 18 + 8), totalR, maxCount / totalR);
        group.padding(Insets.of(4));
        int index = 0;
        for (var entry : map.entrySet()) {
            RecipeCapability<?> cap = entry.getKey();
            if (cap.getWidgetClass() == null) {
                continue;
            }
            int capCount = entry.getValue();
            for (int slotIndex = 0; slotIndex < capCount; slotIndex++) {
                var component = cap.createUIComponent();
                //noinspection DataFlowIssue
                component.positioning(Positioning.absolute((index % 3) * 18, (index / 3) * 18))
                        .id(cap.slotName(isOutputs ? IO.OUT : IO.IN, slotIndex));
                var texture = UIComponents.texture(getOverlaysForSlot(isOutputs, cap, slotIndex == capCount - 1, isSteam, isHighPressure), 18, 18);

                StackLayout layout = UIContainers.stack(Sizing.fixed(18), Sizing.fixed(18));
                layout.children(List.of(component, texture));
                group.child(component, index % 3, index / 3);
                index++;
            }
            // move to new row
            index += (3 - (index % 3)) % 3;
        }
        return group;
    }

    protected static int[] determineSlotsGrid(int itemCount) {
        int itemSlotsToLeft;
        int itemSlotsToDown;
        double sqrt = Math.sqrt(itemCount);
        // if the number of input has an integer root
        // return it.
        if (sqrt % 1 == 0) {
            itemSlotsToLeft = itemSlotsToDown = (int) sqrt;
        } else if (itemCount == 3) {
            itemSlotsToLeft = 3;
            itemSlotsToDown = 1;
        } else {
            // if we couldn't fit all into a perfect square,
            // increase the amount of slots to the left
            itemSlotsToLeft = (int) Math.ceil(sqrt);
            itemSlotsToDown = itemSlotsToLeft - 1;
            // if we still can't fit all the slots in a grid,
            // increase the amount of slots on the bottom
            if (itemCount > itemSlotsToLeft * itemSlotsToDown) {
                itemSlotsToDown = itemSlotsToLeft;
            }
        }
        return new int[] { itemSlotsToLeft, itemSlotsToDown };
    }

    protected UITexture getOverlaysForSlot(boolean isOutput, RecipeCapability<?> capability, boolean isLast,
                                           boolean isSteam, boolean isHighPressure) {
        UITexture base = capability == FluidRecipeCapability.CAP ? GuiTextures.FLUID_SLOT :
                (isSteam ? GuiTextures.SLOT_STEAM.get(isHighPressure) : GuiTextures.SLOT);
        byte overlayKey = (byte) ((isOutput ? 2 : 0) + (capability == FluidRecipeCapability.CAP ? 1 : 0) +
                (isLast ? 4 : 0));
        if (slotOverlays.containsKey(overlayKey)) {
            return UITextures.group(base, slotOverlays.get(overlayKey));
        }
        return base;
    }

    /**
     * @return the height used to determine size of background texture in JEI
     */
    public int getPropertyHeightShift() {
        int maxPropertyCount = maxTooltips + recipeType.getDataInfos().size();
        return maxPropertyCount * 10; // GTRecipeWidget#LINE_HEIGHT
    }

    public void appendJEIUI(GTRecipe recipe, UIComponentGroup widgetGroup) {
        if (uiBuilder != null) {
            uiBuilder.accept(recipe, widgetGroup);
        }
    }

    public GTRecipeTypeUI setSlotOverlay(boolean isOutput, boolean isFluid, UITexture slotOverlay) {
        return this.setSlotOverlay(isOutput, isFluid, false, slotOverlay).setSlotOverlay(isOutput, isFluid, true,
                slotOverlay);
    }

    public GTRecipeTypeUI setSlotOverlay(boolean isOutput, boolean isFluid, boolean isLast, UITexture slotOverlay) {
        this.slotOverlays.put((byte) ((isOutput ? 2 : 0) + (isFluid ? 1 : 0) + (isLast ? 4 : 0)), slotOverlay);
        return this;
    }

    public GTRecipeTypeUI setProgressBar(ResourceTexture progressBar, ProgressTexture.FillDirection moveType) {
        this.progressBarTexture = UITextures.progress(progressBar.getSubTexture(0, 0, 1, 0.5),
                progressBar.getSubTexture(0, 0.5, 1, 0.5)).fillDirection(moveType);
        return this;
    }

}
