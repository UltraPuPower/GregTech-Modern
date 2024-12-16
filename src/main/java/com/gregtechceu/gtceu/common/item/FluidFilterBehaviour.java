package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.cover.filter.FluidFilter;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.Surface;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.holder.HeldItemUIHolder;
import com.gregtechceu.gtceu.api.ui.util.SlotGenerator;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2023/3/13
 * @implNote FluidFilterBehaviour
 */
public record FluidFilterBehaviour(Function<ItemStack, FluidFilter> filterCreator) implements IItemUIFactory {

    @Override
    public void onAttached(Item item) {
        IItemUIFactory.super.onAttached(item);
        FluidFilter.FILTERS.put(item, filterCreator);
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<HeldItemUIHolder> menu, HeldItemUIHolder holder) {
        var held = holder.getHeld();

        var generator = SlotGenerator.begin(menu::addSlot, 0, 0);
        generator.playerInventory(player.getInventory());
        FluidFilter.loadFilter(held).loadServerUI(player, menu, holder);
    }

    @Override
    public void loadClientUI(Player entityPlayer, UIAdapter<UIComponentGroup> adapter, HeldItemUIHolder holder) {
        var rootComponent = UIContainers.group(Sizing.fixed(176), Sizing.fixed(157));
        rootComponent.positioning(Positioning.relative(50, 50));
        adapter.rootComponent.child(rootComponent);

        rootComponent.surface(Surface.UI_BACKGROUND);
        rootComponent.child(UIComponents.label(holder.getHeld().getHoverName())
                .positioning(Positioning.absolute(5, 5)))
                .child(FluidFilter.loadFilter(holder.getHeld()).openConfigurator((176 - 80) / 2, (60 - 55) / 2 + 15,
                        adapter))
                .child(UIComponents.playerInventory(entityPlayer.getInventory(), GuiTextures.SLOT));
    }
}
