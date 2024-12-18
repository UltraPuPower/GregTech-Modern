package com.gregtechceu.gtceu.api.recipe.ui;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
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
import com.gregtechceu.gtceu.api.ui.texture.SteamTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.api.ui.util.UIComponentUtils;
import com.gregtechceu.gtceu.integration.emi.recipe.GTRecipeEMICategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;
import com.gregtechceu.gtceu.integration.rei.recipe.GTRecipeREICategory;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.jei.JEIPlugin;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.google.common.collect.Table;
import dev.emi.emi.api.EmiApi;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

@Slf4j
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
    protected BiConsumer<GTRecipe, StackLayout> uiBuilder;
    @Setter
    @Getter
    protected int maxTooltips = 3;

    @Nullable
    private UIModel customUICache;
    private Size recipeViewerSize;
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
        this.recipeViewerSize = null;
    }

    public Size getRecipeViewerSize() {
        if (this.recipeViewerSize == null) {
            var component = createEditableUITemplate(false, false).createDefault();
            // inflate up to a sane default(?)
            component.inflate(Size.of(200, 200));
            var originalSize = component.fullSize();
            this.originalWidth = originalSize.width();
            this.recipeViewerSize = Size.of(Math.max(originalWidth, 150),
                    getPropertyHeightShift() + 5 + originalSize.height());
        }
        return this.recipeViewerSize;
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
    public StackLayout createUITemplate(DoubleSupplier progressSupplier,
                                             UIAdapter<StackLayout> adapter,
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

    public StackLayout createUITemplate(DoubleSupplier progressSupplier,
                                             UIAdapter<StackLayout> adapter,
                                             Table<IO, RecipeCapability<?>, Object> storages,
                                             CompoundTag data,
                                             List<RecipeCondition> conditions) {
        return createUITemplate(progressSupplier, adapter, storages, data, conditions, false, false);
    }

    /**
     * Auto layout UI template for recipes.
     */
    public IEditableUI<StackLayout, RecipeHolder> createEditableUITemplate(final boolean isSteam,
                                                                                final boolean isHighPressure) {
        return new IEditableUI.Normal<>(() -> {
            var isCustomUI = !isSteam && hasCustomUI();
            if (isCustomUI) {
                UIModel model = getCustomUI();
                StackLayout group = model.parseComponentTree(StackLayout.class);
                group.positioning(Positioning.absolute(0, 0));
                return group;
            }

            var inputs = addInventorySlotGroup(false, isSteam, isHighPressure);
            var outputs = addInventorySlotGroup(true, isSteam, isHighPressure);
            var group = UIContainers.stack(Sizing.fill(), Sizing.fill());

            inputs.positioning(Positioning.relative(35, 45));
            outputs.positioning(Positioning.relative(65, 45));
            group.child(inputs);
            group.child(outputs);

            var progressWidget = UIComponents.progress(ProgressComponent.JEIProgress);
            progressWidget.progressTexture(progressBarTexture)
                    .positioning(Positioning.relative(50, 45))
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
            UIComponentUtils.componentByIdForEach(template, "^progress$", ProgressComponent.class,
                    progressComponent -> {
                        progressComponent.progressSupplier(recipeHolder.progressSupplier);
                        progress.add(progressComponent);
                    });
            // Then set the dual-progress widgets, to override their builtin ones' suppliers, in case someone forgot to
            // remove the id from the internal ones.
            UIComponentUtils.componentByIdForEach(template, "^progress$", DualProgressComponent.class,
                    dualProgressComponent -> {
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
                        UIComponentUtils.componentByIdForEach(template, "^%s.[0-9]+$".formatted(cap.slotName(io)),
                                widgetClass,
                                widget -> {
                                    var index = UIComponentUtils.componentIdIndex(widget);
                                    cap.applyUIComponentInfo(widget, adapter, index, isJEI, io, recipeHolder,
                                            recipeType, null, null,
                                            storage, 0, 0);
                                });
                    }
                }
            }
        });
    }

    protected ParentUIComponent addInventorySlotGroup(boolean isOutputs, boolean isSteam, boolean isHighPressure) {
        TreeMap<RecipeCapability<?>, Integer> map = new TreeMap<>(RecipeCapability.COMPARATOR);
        if (isOutputs) {
            for (var value : recipeType.maxOutputs.entrySet()) {
                if (value.getKey().doRenderSlot) {
                    int val = value.getValue();
                    map.put(value.getKey(), val);
                }
            }
        } else {
            for (var value : recipeType.maxInputs.entrySet()) {
                if (value.getKey().doRenderSlot) {
                    int val = value.getValue();
                    map.put(value.getKey(), val);
                }
            }
        }
        int slotCountTotal = map.values().stream().mapToInt(i -> i).sum();

        int[] inputSlotGrid = determineSlotsGrid(map.getOrDefault(ItemRecipeCapability.CAP, 0));
        int itemSlotsToLeft = inputSlotGrid[0];
        int itemSlotsToDown = inputSlotGrid[1];
        int startInputsX = isOutputs ? 106 : 70 - itemSlotsToLeft * 18;
        int startInputsY = 33 - (int) (itemSlotsToDown / 2.0 * 18);

        boolean wasGroup = slotCountTotal == 12;
        if (wasGroup) startInputsY -= 9;
        else if (slotCountTotal >= 8 && !isOutputs) startInputsY -= 9;

        StackLayout group = UIContainers.stack(Sizing.content(4), Sizing.content(8));
        group.positioning(Positioning.absolute(startInputsX, startInputsY));
        group.padding(Insets.of(4));
        for (var entry : map.entrySet()) {
            RecipeCapability<?> cap = entry.getKey();
            if (cap.getWidgetClass() == null) {
                continue;
            }
            int capCount = entry.getValue();

            // prioritize item slots
            if (cap == ItemRecipeCapability.CAP) {
                for (int i = 0; i < itemSlotsToDown; i++) {
                    for (int j = 0; j < itemSlotsToLeft; j++) {
                        int slotIndex = i * itemSlotsToLeft + j;
                        if (slotIndex >= capCount) break;
                        int x = 18 * j;
                        int y = 18 * i;

                        addSlot(group, cap, capCount, slotIndex, x, y, isOutputs, isSteam, isHighPressure);
                    }
                }
            } else { // add all other slots after
                int offset = wasGroup ? 2 : 0;

                if (itemSlotsToDown >= capCount && itemSlotsToLeft < 3) {
                    int startSpecX = isOutputs ? itemSlotsToLeft * 18 : -18;
                    for (int i = 0; i < capCount; i++) {
                        int y = offset + 18 * i;

                        addSlot(group, cap, capCount, i, startSpecX, y, isOutputs, isSteam, isHighPressure);
                    }
                } else {
                    int startSpecY = itemSlotsToDown * 18;
                    for (int i = 0; i < capCount; i++) {
                        int x = isOutputs ? 18 * (i % 3) :
                                itemSlotsToLeft * 18 - 18 - 18 * (i % 3);
                        int y = startSpecY + (i / 3) * 18;

                        addSlot(group, cap, capCount, i, x, y, isOutputs, isSteam, isHighPressure);
                    }
                }

            }

        }
        return group;
    }

    private void addSlot(StackLayout group, RecipeCapability<?> cap, int capCount,
                         int index, int x, int y,
                         boolean isOutputs, boolean isSteam, boolean isHighPressure) {

        var component = cap.createUIComponent();
        // noinspection DataFlowIssue
        component.id(cap.slotName(isOutputs ? IO.OUT : IO.IN, index))
                .positioning(Positioning.absolute(0, 0));
        var texture = UIComponents.texture(
                        getOverlaysForSlot(isOutputs, cap, index == capCount - 1, isSteam, isHighPressure))
                .sizing(Sizing.fill())
                .positioning(Positioning.absolute(0, 0));

        StackLayout layout = UIContainers.stack(Sizing.fixed(18), Sizing.fixed(18));
        layout.positioning(Positioning.absolute(x, y));
        layout.children(List.of(texture, component));
        group.child(layout);
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
        return maxPropertyCount * 10; // GTRecipeComponent#LINE_HEIGHT
    }

    public void appendJEIUI(GTRecipe recipe, StackLayout widgetGroup) {
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
