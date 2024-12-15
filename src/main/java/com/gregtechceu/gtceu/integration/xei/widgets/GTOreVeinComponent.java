package com.gregtechceu.gtceu.integration.xei.widgets;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.DimensionMarker;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.bedrockfluid.BedrockFluidDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.bedrockore.BedrockOreDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.api.ui.component.SlotComponent;
import com.gregtechceu.gtceu.api.ui.component.TankComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.core.Insets;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.client.ClientProxy;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Arbor
 * @implNote GTOreVeinComponent
 */
@Getter
public class GTOreVeinComponent extends FlowLayout {

    @Nullable
    @Getter
    private GTOreDefinition ore;
    @Nullable
    @Getter
    private BedrockFluidDefinition fluid;
    @Nullable
    @Getter
    private BedrockOreDefinition bedrockOre;
    private final String name;
    private final int weight;
    private final String range;
    private final Set<ResourceKey<Level>> dimensionFilter;
    public final static int width = 120;

    public GTOreVeinComponent(@NotNull GTOreDefinition ore) {
        super(Sizing.fixed(width), Sizing.fixed(160), Algorithm.VERTICAL);
        padding(Insets.both(5, 0));

        this.ore = ore;
        this.name = getOreName(ore);
        this.weight = ore.weight();
        this.dimensionFilter = ore.dimensionFilter();
        this.range = range(ore);
        setupBaseGui(ore);
        setupText(ore);
    }

    public GTOreVeinComponent(@NotNull BedrockFluidDefinition fluid) {
        super(Sizing.fixed(width), Sizing.fixed(140), Algorithm.VERTICAL);
        padding(Insets.both(5, 0));

        this.fluid = fluid;
        this.name = getFluidName(fluid);
        this.weight = fluid.getWeight();
        this.dimensionFilter = fluid.getDimensionFilter();
        this.range = "NULL";
        setupBaseGui(fluid);
        setupText(fluid);
    }

    public GTOreVeinComponent(@NotNull BedrockOreDefinition bedrockOre) {
        super(Sizing.fixed(width), Sizing.fixed(140), Algorithm.VERTICAL);
        padding(Insets.both(5, 0));

        this.bedrockOre = bedrockOre;
        this.name = getBedrockOreName(bedrockOre);
        this.weight = bedrockOre.weight();
        this.dimensionFilter = bedrockOre.dimensionFilter();
        this.range = "NULL";
        setupBaseGui(bedrockOre);
        setupText(bedrockOre);
    }

    @SuppressWarnings("all")
    private String range(GTOreDefinition oreDefinition) {
        HeightProvider height = oreDefinition.range().height;
        int minHeight = 0, maxHeight = 0;
        if (height instanceof UniformHeight uniform) {
            minHeight = uniform.minInclusive.resolveY(null);
            maxHeight = uniform.maxInclusive.resolveY(null);
        }
        return String.format("%d - %d", minHeight, maxHeight);
    }

    private void setupBaseGui(GTOreDefinition oreDefinition) {
        NonNullList<ItemStack> containedOresAsItemStacks = NonNullList.create();
        List<Integer> chances = oreDefinition.veinGenerator().getAllChances();
        containedOresAsItemStacks.addAll(getRawMaterialList(oreDefinition));
        var handler = new CustomItemStackHandler(containedOresAsItemStacks);

        int n = containedOresAsItemStacks.size();
        int x = (width - 18 * n) / 2 - 5;
        for (int i = 0; i < n; i++) {
            SlotComponent oreSlot = (SlotComponent) UIComponents.slot(handler, i)
                    .canInsert(false)
                    .canExtract(false)
                    .positioning(Positioning.absolute(x, 18));
            int finalIndex = i;
            oreSlot.tooltip((stack, tooltips) -> tooltips.add(
                    Component.translatable("gtceu.jei.ore_vein_diagram.chance", chances.get(finalIndex))));
            oreSlot.ingredientIO(IO.OUT);
            child(oreSlot);
            x += 18;
        }
    }

    private void setupBaseGui(BedrockFluidDefinition fluid) {
        Fluid storedFluid = fluid.getStoredFluid().get();
        TankComponent fluidSlot = (TankComponent) UIComponents.tank(
                        new CustomFluidTank(new FluidStack(storedFluid, 1000)))
                .canInsert(false)
                .canExtract(false)
                .positioning(Positioning.absolute(46, 18));
        fluidSlot.ingredientIO(IO.OUT);
        child(fluidSlot);
    }

    private void setupBaseGui(BedrockOreDefinition bedrockOreDefinition) {
        NonNullList<ItemStack> containedOresAsItemStacks = NonNullList.create();
        List<Integer> chances = bedrockOreDefinition.getAllChances();
        containedOresAsItemStacks.addAll(getRawMaterialList(bedrockOreDefinition));
        var handler = new CustomItemStackHandler(containedOresAsItemStacks);

        int n = containedOresAsItemStacks.size();
        int x = (width - 18 * n) / 2 - 5;
        for (int i = 0; i < n; i++) {
            SlotComponent oreSlot = (SlotComponent) UIComponents.slot(handler, i)
                    .canInsert(false)
                    .canExtract(false)
                    .positioning(Positioning.absolute(x, 18));
            int finalIndex = i;
            oreSlot.tooltip((stack, tooltips) ->
                    tooltips.add(Component.translatable("gtceu.jei.ore_vein_diagram.chance", chances.get(finalIndex))));
            oreSlot.ingredientIO(IO.OUT);
            child(oreSlot);
            x += 18;
        }
    }

    private void setupText(GTOreDefinition ignored) {
        child(UIComponents.label(Component.translatable("gtceu.jei.ore_vein." + name))
                .textType(com.gregtechceu.gtceu.api.ui.texture.TextTexture.TextType.LEFT_ROLL)
                .maxWidth(width - 10)
                .positioning(Positioning.absolute(width - 10, 16)));
        child(UIComponents.label(Component.translatable("gtceu.jei.ore_vein_diagram.spawn_range"))
                .verticalSizing(Sizing.fixed(10)));
        child(UIComponents.label(Component.literal(range))
                .verticalSizing(Sizing.fixed(10)));

        child(UIComponents.label(Component.translatable("gtceu.jei.ore_vein_diagram.weight", weight))
                .verticalSizing(Sizing.fixed(10)));
        child(UIComponents.label(Component.translatable("gtceu.jei.ore_vein_diagram.dimensions"))
                .verticalSizing(Sizing.fixed(10)));
        setupDimensionMarker(80);
    }

    private void setupText(BedrockFluidDefinition ignored) {
        child(UIComponents.label(Component.translatable("gtceu.jei.bedrock_fluid." + name))
                .textType(com.gregtechceu.gtceu.api.ui.texture.TextTexture.TextType.LEFT_ROLL)
                .maxWidth(width - 10)
                .positioning(Positioning.absolute(width - 10, 16)));
        child(UIComponents.label(Component.translatable("gtceu.jei.ore_vein_diagram.weight", weight))
                .verticalSizing(Sizing.fixed(10)));
        child(UIComponents.label(Component.translatable("gtceu.jei.ore_vein_diagram.dimensions"))
                .verticalSizing(Sizing.fixed(10)));
        setupDimensionMarker(60);
    }

    private void setupText(BedrockOreDefinition ignored) {
        child(UIComponents.label(Component.translatable("gtceu.jei.bedrock_ore." + name))
                .textType(com.gregtechceu.gtceu.api.ui.texture.TextTexture.TextType.LEFT_ROLL)
                .maxWidth(width - 10)
                .positioning(Positioning.absolute(width - 10, 16)));
        child(UIComponents.label(Component.translatable("gtceu.jei.ore_vein_diagram.weight", weight))
                .verticalSizing(Sizing.fixed(10)));
        child(UIComponents.label(Component.translatable("gtceu.jei.ore_vein_diagram.dimensions"))
                .verticalSizing(Sizing.fixed(10)));
        setupDimensionMarker(60);
    }

    private void setupDimensionMarker(int yPosition) {
        if (this.dimensionFilter != null) {
            int interval = 2;
            int rowSlots = (width - 10 + interval) / (16 + interval);

            DimensionMarker[] dimMarkers = dimensionFilter.stream()
                    .map(ResourceKey::location)
                    .map(loc -> GTRegistries.DIMENSION_MARKERS.getOrDefault(loc,
                            new DimensionMarker(DimensionMarker.MAX_TIER, () -> Blocks.BARRIER, loc.toString())))
                    .sorted(Comparator.comparingInt(DimensionMarker::getTier))
                    .toArray(DimensionMarker[]::new);
            var handler = new CustomItemStackHandler(dimMarkers.length);
            for (int i = 0; i < dimMarkers.length; i++) {
                var dimMarker = dimMarkers[i];
                var icon = dimMarker.getIcon();
                int row = Math.floorDiv(i, rowSlots);
                SlotComponent dimSlot = (SlotComponent) UIComponents.slot(handler, i)
                        .canInsert(false)
                        .canExtract(false)
                        .ingredientIO(IO.NONE)
                        .positioning(Positioning.absolute(5 + (16 + interval) * (i - row * rowSlots),
                                yPosition + 18 * row));
                handler.setStackInSlot(i, icon);
                if (ConfigHolder.INSTANCE.compat.showDimensionTier) {
                    dimSlot.overlayTexture(
                            UITextures.text(Component.literal("T" + (dimMarker.tier >= DimensionMarker.MAX_TIER ? "?" : dimMarker.tier)))
                                    .scale(0.75F)
                                    .transform(-3F, 5F));
                }
                child(dimSlot.backgroundTexture(UITexture.EMPTY));
            }
        } else {
            // FIXME MAKE TRANSLATABLE
            child(UIComponents.label(Component.translatable("Any"))
                    .positioning(Positioning.absolute(5, yPosition)));
        }
    }

    public static List<ItemStack> getContainedOresAndBlocks(GTOreDefinition oreDefinition) {
        return oreDefinition.veinGenerator().getAllEntries().stream()
                .flatMap(entry -> entry.getKey().map(state -> Stream.of(state.getBlock().asItem().getDefaultInstance()),
                        material -> {
                            Set<ItemStack> ores = new HashSet<>();
                            ores.add(ChemicalHelper.get(TagPrefix.rawOre, material));
                            for (TagPrefix prefix : TagPrefix.ORES.keySet()) {
                                ores.add(ChemicalHelper.get(prefix, material));
                            }
                            return ores.stream();
                        }))
                .toList();
    }

    public static List<ItemStack> getRawMaterialList(GTOreDefinition oreDefinition) {
        return oreDefinition.veinGenerator().getAllEntries().stream()
                .map(entry -> entry.getKey().map(state -> state.getBlock().asItem().getDefaultInstance(),
                        material -> ChemicalHelper.get(TagPrefix.rawOre, material)))
                .toList();
    }

    public static List<ItemStack> getRawMaterialList(BedrockOreDefinition bedrockOreDefinition) {
        return bedrockOreDefinition.materials().stream()
                .map(entry -> ChemicalHelper.get(TagPrefix.rawOre, entry.getFirst()))
                .toList();
    }

    public static String getOreName(GTOreDefinition oreDefinition) {
        ResourceLocation id = ClientProxy.CLIENT_ORE_VEINS.inverse().get(oreDefinition);
        return id.getPath();
    }

    public static String getFluidName(BedrockFluidDefinition fluid) {
        ResourceLocation id = ClientProxy.CLIENT_FLUID_VEINS.inverse().get(fluid);
        return id.getPath();
    }

    public static String getBedrockOreName(BedrockOreDefinition oreDefinition) {
        ResourceLocation id = ClientProxy.CLIENT_BEDROCK_ORE_VEINS.inverse().get(oreDefinition);
        return id.getPath();
    }

}
