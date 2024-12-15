package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.cover.filter.ItemFilter;
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
 * @implNote ItemFilterBehaviour
 */
public record ItemFilterBehaviour(Function<ItemStack, ItemFilter> filterCreator) implements IItemUIFactory {

    @Override
    public void onAttached(Item item) {
        IItemUIFactory.super.onAttached(item);
        ItemFilter.FILTERS.put(item, filterCreator);
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<HeldItemUIHolder> menu, HeldItemUIHolder holder) {
        ItemFilter.loadFilter(holder.getHeld()).loadServerUI(player, menu, holder);
        var generator = SlotGenerator.begin(menu::addSlot, 0, 0);
        generator.playerInventory(player.getInventory());
    }

    @Override
    public void loadClientUI(Player entityPlayer, UIAdapter<UIComponentGroup> adapter, HeldItemUIHolder holder) {
        var group = UIContainers.group(Sizing.fixed(176), Sizing.fixed(157));
        group.surface(Surface.UI_BACKGROUND);

        group.child(UIComponents.label(holder.getHeld().getHoverName()))
                .child(ItemFilter.loadFilter(holder.getHeld()).openConfigurator((176 - 80) / 2, (60 - 55) / 2 + 15, adapter))
                .child(UIComponents.playerInventory(entityPlayer.getInventory(), GuiTextures.SLOT)
                        .positioning(Positioning.absolute(7,  75)));
    }

}
