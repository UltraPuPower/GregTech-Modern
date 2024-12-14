package com.gregtechceu.gtceu.api.cover.filter;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.lookup.GTRecipeLookup;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.EnumSelectorComponent;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.holder.HeldItemUIHolder;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.utils.ItemStackHashStrategy;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import lombok.Setter;

import java.util.List;
import java.util.function.Consumer;

public class SmartItemFilter implements ItemFilter {

    protected Consumer<ItemFilter> itemWriter = filter -> {};
    protected Consumer<ItemFilter> onUpdated = filter -> itemWriter.accept(filter);

    @Setter
    private SmartFilteringMode filterMode = SmartFilteringMode.ELECTROLYZER;

    protected SmartItemFilter() {}

    public static SmartItemFilter loadFilter(ItemStack itemStack) {
        return loadFilter(itemStack.getOrCreateTag(), filter -> itemStack.setTag(filter.saveFilter()));
    }

    private static SmartItemFilter loadFilter(CompoundTag tag, Consumer<ItemFilter> itemWriter) {
        var handler = new SmartItemFilter();
        handler.itemWriter = itemWriter;
        handler.filterMode = SmartFilteringMode.VALUES[tag.getInt("filterMode")];
        return handler;
    }

    @Override
    public void setOnUpdated(Consumer<ItemFilter> onUpdated) {
        this.onUpdated = filter -> {
            this.itemWriter.accept(filter);
            onUpdated.accept(filter);
        };
    }

    @Override
    public CompoundTag saveFilter() {
        var tag = new CompoundTag();
        tag.putInt("filterMode", filterMode.ordinal());
        return tag;
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<HeldItemUIHolder> menu, HeldItemUIHolder holder) {
        // TODO implement
    }

    @Override
    public UIComponent openConfigurator(int x, int y, UIAdapter<UIComponentGroup> adapter) {
        FlowLayout group = UIContainers.verticalFlow(Sizing.fixed(18 * 3 + 25), Sizing.fixed(18 * 3));
        group.padding(Insets.both(16, 8));
        group.positioning(Positioning.absolute(x, y));
        group.child(new EnumSelectorComponent<>(Sizing.fixed(32), Sizing.fixed(32),
                SmartFilteringMode.VALUES, filterMode, this::setFilterMode));
        return group;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return testItemCount(itemStack) > 0;
    }

    @Override
    public int testItemCount(ItemStack itemStack) {
        return filterMode.cache.computeIfAbsent(itemStack, this::lookup);
    }

    private int lookup(ItemStack itemStack) {
        ItemStack copy = itemStack.copyWithCount(Integer.MAX_VALUE);
        var ingredients = ItemRecipeCapability.CAP.convertToMapIngredient(copy);
        var recipe = filterMode.lookup.recurseIngredientTreeFindRecipe(List.of(ingredients),
                filterMode.lookup.getLookup(), r -> true);
        if (recipe == null) return 0;
        for (Content content : recipe.getInputContents(ItemRecipeCapability.CAP)) {
            var stacks = ItemRecipeCapability.CAP.of(content.getContent()).getItems();
            for (var stack : stacks) {
                if (ItemStack.isSameItem(stack, itemStack)) return stack.getCount();
            }
        }
        return 0;
    }

    public void setModeFromMachine(String machineName) {
        for (SmartFilteringMode mode : SmartFilteringMode.VALUES) {
            if (machineName.contains(mode.localeName)) {
                this.filterMode = mode;
                return;
            }
        }
    }

    @MethodsReturnNonnullByDefault
    private enum SmartFilteringMode implements EnumSelectorComponent.SelectableEnum {

        ELECTROLYZER("electrolyzer", GTRecipeTypes.ELECTROLYZER_RECIPES),
        CENTRIFUGE("centrifuge", GTRecipeTypes.CENTRIFUGE_RECIPES),
        SIFTER("sifter", GTRecipeTypes.SIFTER_RECIPES);

        private static final SmartFilteringMode[] VALUES = values();
        private final String localeName;
        private final GTRecipeLookup lookup;
        private final Object2IntOpenCustomHashMap<ItemStack> cache = new Object2IntOpenCustomHashMap<>(
                ItemStackHashStrategy.comparingAllButCount());

        SmartFilteringMode(String localeName, GTRecipeType type) {
            this.localeName = localeName;
            this.lookup = type.getLookup();
        }

        @Override
        public String getTooltip() {
            return "cover.item_smart_filter.filtering_mode." + localeName;
        }

        @Override
        public UITexture getIcon() {
            return UITextures.resource(GTCEu.id("textures/block/machines/" + localeName + "/overlay_front.png"));
        }
    }
}
