package com.gregtechceu.gtceu.api.cover;

import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.holder.IUIHolder;

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

    ParentUIComponent createUIWidget();

    @Override
    default void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter) {
        var rootComponent = adapter.rootComponent;

        var component = createUIWidget();
        component.positioning(Positioning.absolute((176 - rootComponent.width()) / 2, 0));
        component.surface(Surface.UI_BACKGROUND);
        rootComponent.child(UIComponents.playerInventory(adapter.screen().getMenu(), 0));
        rootComponent.child(component);
    }

}
