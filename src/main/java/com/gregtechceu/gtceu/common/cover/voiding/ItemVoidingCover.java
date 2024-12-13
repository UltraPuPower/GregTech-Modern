package com.gregtechceu.gtceu.common.cover.voiding;

import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.cover.IUICover;
import com.gregtechceu.gtceu.api.cover.filter.ItemFilter;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.ToggleButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Insets;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.common.cover.ConveyorCover;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemVoidingCover extends ConveyorCover implements IUICover, IControllable {

    @Persisted
    @Getter
    protected boolean isEnabled = false;

    public ItemVoidingCover(CoverDefinition definition, ICoverable coverHolder, Direction attachedSide) {
        super(definition, coverHolder, attachedSide, 0);
    }

    @Override
    protected boolean isSubscriptionActive() {
        return isWorkingEnabled() && isEnabled();
    }

    //////////////////////////////////////////////
    // *********** COVER LOGIC ***********//
    //////////////////////////////////////////////

    @Override
    protected void update() {
        if (coverHolder.getOffsetTimer() % 5 != 0)
            return;

        doVoidItems();
        subscriptionHandler.updateSubscription();
    }

    protected void doVoidItems() {
        IItemHandler handler = getOwnItemHandler();
        if (handler == null) {
            return;
        }
        voidAny(handler);
    }

    void voidAny(IItemHandler handler) {
        ItemFilter filter = filterHandler.getFilter();

        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack sourceStack = handler.extractItem(slot, Integer.MAX_VALUE, true);
            if (sourceStack.isEmpty() || !filter.test(sourceStack)) {
                continue;
            }
            handler.extractItem(slot, Integer.MAX_VALUE, false);
        }
    }

    public void setWorkingEnabled(boolean workingEnabled) {
        isWorkingEnabled = workingEnabled;
        subscriptionHandler.updateSubscription();
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        subscriptionHandler.updateSubscription();
    }

    //////////////////////////////////////
    // *********** GUI ***********//
    /// ///////////////////////////////////

    @Override
    public ParentUIComponent createUIWidget() {
        final var group = UIContainers.group(Sizing.fixed(176), Sizing.fixed(120));
        group.padding(Insets.both(10, 5));
        group.child(UIComponents.label(Component.translatable(getUITitle())));

        group.child(new ToggleButtonComponent(GuiTextures.BUTTON_POWER, this::isEnabled, this::setEnabled)
                .positioning(Positioning.absolute(0, 5))
                .sizing(Sizing.fixed(20)));

        // group.child(filterHandler.createFilterSlotUI(36, 21));
        group.child(filterHandler.createFilterSlotUI(148, 91));
        group.child(filterHandler.createFilterConfigUI(10, 50, 126, 60));

        buildAdditionalUI(group);

        return group;
    }

    @NotNull
    protected String getUITitle() {
        return "cover.item.voiding.title";
    }

    protected void buildAdditionalUI(UIComponentGroup group) {
        // Do nothing in the base implementation. This is intended to be overridden by subclasses.
    }

    protected void configureFilter() {
        // Do nothing in the base implementation. This is intended to be overridden by subclasses.
    }

    //////////////////////////////////////
    // ***** LDLib SyncData ******//
    //////////////////////////////////////

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ItemVoidingCover.class,
            ConveyorCover.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
