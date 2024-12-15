package com.gregtechceu.gtceu.api.cover;

import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.holder.IUIHolder;

import com.gregtechceu.gtceu.api.ui.util.SlotGenerator;
import net.minecraft.world.entity.player.Player;

/**
 * @author KilaBash
 * @date 2023/3/12
 * @implNote IUICover
 */
public interface IUICover extends IUIHolder<CoverBehavior> {

    default CoverBehavior self() {
        return (CoverBehavior) this;
    }

    @Override
    default boolean isInvalid() {
        return self().coverHolder.isInValid() || self().coverHolder.getCoverAtSide(self().attachedSide) != self();
    }

    @Override
    default boolean isClientSide() {
        return self().coverHolder.isRemote();
    }

    @Override
    default void markDirty() {
        self().coverHolder.markDirty();
    }

    ParentUIComponent createUIWidget(UIAdapter<UIComponentGroup> adapter);

    @Override
    default void loadServerUI(Player player, UIContainerMenu<CoverBehavior> menu, CoverBehavior holder) {
        var generator = SlotGenerator.begin(menu::addSlot, 0, 0);
        generator.playerInventory(player.getInventory());
    }

    @Override
    default void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, CoverBehavior holder) {
        var rootComponent = adapter.rootComponent;

        var component = createUIWidget(adapter);
        component.positioning(Positioning.absolute((176 - rootComponent.width()) / 2, 0));
        component.surface(Surface.UI_BACKGROUND);
        rootComponent.child(UIComponents.playerInventory(player.getInventory(), GuiTextures.SLOT));
        rootComponent.child(component);
    }

}
