package com.gregtechceu.gtceu.common.cover.detector;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.cover.IUICover;
import com.gregtechceu.gtceu.api.cover.filter.FilterHandler;
import com.gregtechceu.gtceu.api.cover.filter.FilterHandlers;
import com.gregtechceu.gtceu.api.cover.filter.ItemFilter;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.IntInputComponent;
import com.gregtechceu.gtceu.api.ui.component.ToggleButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.gregtechceu.gtceu.utils.RedstoneUtil;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedItemDetectorCover extends ItemDetectorCover implements IUICover {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedItemDetectorCover.class, DetectorCover.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private static final int DEFAULT_MIN = 64;
    private static final int DEFAULT_MAX = 512;
    @Persisted
    @Getter
    @Setter
    private int outputAmount;
    @Persisted
    @Getter
    private int minValue, maxValue;

    @Persisted
    @DescSynced
    @Getter
    protected final FilterHandler<ItemStack, ItemFilter> filterHandler;

    public AdvancedItemDetectorCover(CoverDefinition definition, ICoverable coverHolder, Direction attachedSide) {
        super(definition, coverHolder, attachedSide);

        this.minValue = DEFAULT_MIN;
        this.maxValue = DEFAULT_MAX;

        filterHandler = FilterHandlers.item(this);
    }

    @Override
    public List<ItemStack> getAdditionalDrops() {
        var list = super.getAdditionalDrops();
        if (!filterHandler.getFilterItem().isEmpty()) {
            list.add(filterHandler.getFilterItem());
        }
        return list;
    }

    @Override
    protected void update() {
        if (this.coverHolder.getOffsetTimer() % 20 != 0)
            return;

        ItemFilter filter = filterHandler.getFilter();
        IItemHandler handler = getItemHandler();
        if (handler == null)
            return;

        int storedItems = 0;

        for (int i = 0; i < handler.getSlots(); i++) {
            if (filter.test(handler.getStackInSlot(i)))
                storedItems += handler.getStackInSlot(i).getCount();
        }

        setRedstoneSignalOutput(
                this.outputAmount = RedstoneUtil.computeLatchedRedstoneBetweenValues(storedItems, maxValue, minValue,
                        isInverted(), this.outputAmount));
    }

    public void setMinValue(int minValue) {
        this.minValue = Mth.clamp(minValue, 0, maxValue - 1);
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = Math.max(maxValue, 0);
    }

    //////////////////////////////////////
    // *********** GUI ***********//
    //////////////////////////////////////

    @Override
    public ParentUIComponent createUIWidget(UIAdapter<StackLayout> adapter) {
        var group = UIContainers.stack(Sizing.fixed(176), Sizing.fixed(170));
        group.padding(Insets.both(10, 0));
        group.child(UIComponents.label(Component.translatable("cover.advanced_item_detector.label"))
                .positioning(Positioning.absolute(0, 5)));

        group.child(UIComponents.label(Component.translatable("cover.advanced_item_detector.min"))
                .positioning(Positioning.absolute(0, 55))
                .horizontalSizing(Sizing.fixed(65)));

        group.child(UIComponents.label(Component.translatable("cover.advanced_item_detector.max"))
                .positioning(Positioning.absolute(0, 80))
                .horizontalSizing(Sizing.fixed(65)));

        group.child(new IntInputComponent(Sizing.fixed(176 - 80 - 10), Sizing.fixed(20), this::getMinValue,
                this::setMinValue)
                .positioning(Positioning.absolute(70, 50)));
        group.child(new IntInputComponent(Sizing.fixed(176 - 80 - 10), Sizing.fixed(20), this::getMaxValue,
                this::setMaxValue)
                .positioning(Positioning.absolute(70, 75)));

        // Invert Redstone Output Toggle:
        group.child(new ToggleButtonComponent(GuiTextures.INVERT_REDSTONE_BUTTON, this::isInverted, this::setInverted) {

            @Override
            public void update(float delta, int mouseX, int mouseY) {
                super.update(delta, mouseX, mouseY);
                tooltip(LangHandler.getMultiLang(
                        "cover.advanced_fluid_detector.invert." + (pressed ? "enabled" : "disabled")));
            }
        }.positioning(Positioning.absolute(-1, 20))
                .sizing(Sizing.fixed(20)));

        group.child(filterHandler.createFilterSlotUI(148, 100));
        group.child(filterHandler.createFilterConfigUI(10, 100, 156, 60, adapter));

        return group;
    }
}
