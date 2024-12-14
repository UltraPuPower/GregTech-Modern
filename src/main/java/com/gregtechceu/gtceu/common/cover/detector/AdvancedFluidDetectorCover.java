package com.gregtechceu.gtceu.common.cover.detector;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.cover.IUICover;
import com.gregtechceu.gtceu.api.cover.filter.FilterHandler;
import com.gregtechceu.gtceu.api.cover.filter.FilterHandlers;
import com.gregtechceu.gtceu.api.cover.filter.FluidFilter;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.IntInputComponent;
import com.gregtechceu.gtceu.api.ui.component.ToggleButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.gregtechceu.gtceu.utils.GTMath;
import com.gregtechceu.gtceu.utils.RedstoneUtil;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedFluidDetectorCover extends FluidDetectorCover implements IUICover {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedFluidDetectorCover.class, DetectorCover.MANAGED_FIELD_HOLDER);

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
    protected final FilterHandler<FluidStack, FluidFilter> filterHandler;

    public AdvancedFluidDetectorCover(CoverDefinition definition, ICoverable coverHolder, Direction attachedSide) {
        super(definition, coverHolder, attachedSide);

        this.minValue = DEFAULT_MIN;
        this.maxValue = DEFAULT_MAX;

        filterHandler = FilterHandlers.fluid(this);
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

        FluidFilter filter = filterHandler.getFilter();
        IFluidHandler fluidHandler = getFluidHandler();
        if (fluidHandler == null)
            return;

        long storedFluid = 0;

        for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
            FluidStack content = fluidHandler.getFluidInTank(tank);

            if (!content.isEmpty() && filter.test(content))
                storedFluid += content.getAmount();
        }

        setRedstoneSignalOutput(
                this.outputAmount = RedstoneUtil.computeLatchedRedstoneBetweenValues(storedFluid, maxValue, minValue,
                        isInverted(), this.outputAmount));
    }

    public void setMinValue(int minValue) {
        this.minValue = GTMath.clamp(minValue, 0, maxValue - 1);
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = Math.max(maxValue, 0);
    }

    //////////////////////////////////////
    // *********** GUI ***********//
    //////////////////////////////////////

    @Override
    public ParentUIComponent createUIWidget(UIAdapter<UIComponentGroup> adapter) {
        var group = UIContainers.group(Sizing.fixed(176), Sizing.fixed(170));
        group.padding(Insets.both(10, 0));
        group.child(UIComponents.label(Component.translatable("cover.advanced_fluid_detector.label"))
                .positioning(Positioning.absolute(0, 5)));

        group.child(UIComponents.label(Component.translatable("cover.advanced_fluid_detector.min"))
                .positioning(Positioning.absolute(0, 55))
                .horizontalSizing(Sizing.fixed(65)));

        group.child(UIComponents.label(Component.translatable("cover.advanced_fluid_detector.max"))
                .positioning(Positioning.absolute(0, 80))
                .horizontalSizing(Sizing.fixed(65)));

        group.child(new IntInputComponent(Sizing.fixed(176 - 80 - 10), Sizing.fixed(20), this::getMinValue, this::setMinValue)
                .positioning(Positioning.absolute(70, 50)));
        group.child(new IntInputComponent(Sizing.fixed(176 - 80 - 10), Sizing.fixed(20), this::getMaxValue, this::setMaxValue)
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
