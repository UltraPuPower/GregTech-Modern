package com.gregtechceu.gtceu.common.machine.storage;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.PhantomFluidComponent;
import com.gregtechceu.gtceu.api.ui.component.TextBoxComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.fancy.FancyMachineUIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemHandlerHelper;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class CreativeTankMachine extends QuantumTankMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CreativeTankMachine.class,
            QuantumTankMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    @DropSaved
    private int mBPerCycle = 1000;
    @Getter
    @Persisted
    @DropSaved
    private int ticksPerCycle = 1;

    public CreativeTankMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.MAX, 1);
    }

    protected FluidCache createCacheFluidHandler(Object... args) {
        return new InfiniteCache(this);
    }

    protected void checkAutoOutput() {
        if (getOffsetTimer() % ticksPerCycle == 0) {
            if (isAutoOutputFluids() && getOutputFacingFluids() != null) {
                cache.exportToNearby(getOutputFacingFluids());
            }
            updateAutoOutputSubscription();
        }
    }

    private InteractionResult updateStored(FluidStack fluid) {
        stored = new FluidStack(fluid, 1000);
        onFluidChanged();
        return InteractionResult.SUCCESS;
    }

    private void setTicksPerCycle(String value) {
        if (value.isEmpty()) return;
        ticksPerCycle = Integer.parseInt(value);
        onFluidChanged();
    }

    private void setmBPerCycle(String value) {
        if (value.isEmpty()) return;
        mBPerCycle = Integer.parseInt(value);
        onFluidChanged();
    }

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                   BlockHitResult hit) {
        var heldItem = player.getItemInHand(hand);
        if (hit.getDirection() == getFrontFacing() && !isRemote()) {
            // Clear fluid if empty + shift-rclick
            if (heldItem.isEmpty()) {
                if (player.isCrouching() && !stored.isEmpty()) {
                    return updateStored(FluidStack.EMPTY);
                }
                return InteractionResult.PASS;
            }

            // If no fluid set and held-item has fluid, set fluid
            if (stored.isEmpty()) {
                return FluidUtil.getFluidContained(heldItem)
                        .map(this::updateStored)
                        .orElse(InteractionResult.PASS);
            }

            // Need to make a fake source to fully fill held-item since our cache only allows mbPerTick extraction
            CustomFluidTank source = new CustomFluidTank(new FluidStack(stored, Integer.MAX_VALUE));
            ItemStack result = FluidUtil.tryFillContainer(heldItem, source, Integer.MAX_VALUE, player, true)
                    .getResult();
            if (!result.isEmpty() && heldItem.getCount() > 1) {
                ItemHandlerHelper.giveItemToPlayer(player, result);
                result = heldItem.copy();
                result.shrink(1);
            }

            if (!result.isEmpty()) {
                player.setItemInHand(hand, result);
                return InteractionResult.SUCCESS;
            } else {
                return FluidUtil.getFluidContained(heldItem)
                        .map(this::updateStored)
                        .orElse(InteractionResult.PASS);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        // TODO implement
        // NEEDS MESSAGES FOR TICKS/ITEMS PER CYCLE & ACTIVE STATE, REMEMBER TO ADD THOSE!!
        super.loadServerUI(player, menu, holder);
    }

    @Override
    public ParentUIComponent createBaseUIComponent(FancyMachineUIComponent component) {
        var group = UIContainers.verticalFlow(Sizing.fixed(176), Sizing.fixed(131));
        group.padding(Insets.both(7, 9));
        group.child(new PhantomFluidComponent(cache, 0, this::getStored, this::updateStored)
                .showAmount(false)
                .positioning(Positioning.absolute(29, -3)))
                .child(UIComponents.label(Component.translatable("gtceu.creative.tank.fluid")))
                .child(UIComponents.texture(GuiTextures.DISPLAY)
                        .sizing(Sizing.fixed(154), Sizing.fixed(14)))
                .child(UIComponents.textBox(Sizing.fixed(152))
                        .textSupplier(() -> String.valueOf(mBPerCycle))
                        .<TextBoxComponent>configure(c -> {
                            c.onChanged().subscribe(this::setmBPerCycle);
                            c.setMaxLength(11);
                        }).numbersOnly(1, Integer.MAX_VALUE))
                .child(UIComponents.label(Component.translatable("gtceu.creative.tank.mbpc")))
                .child(UIContainers.horizontalFlow(Sizing.fixed(154), Sizing.fixed(14))
                        .<FlowLayout>configure(c -> {
                            c.positioning(Positioning.absolute(0, 73));
                            c.surface(Surface.UI_DISPLAY);
                        })
                        .child(UIComponents.textBox(Sizing.fixed(152))
                                .textSupplier(() -> String.valueOf(ticksPerCycle))
                                .<TextBoxComponent>configure(c -> {
                                    c.onChanged().subscribe(this::setTicksPerCycle);
                                    c.setMaxLength(11);
                                })
                                .numbersOnly(1, Integer.MAX_VALUE)
                                .positioning(Positioning.absolute(2, 11))))
                .child(UIComponents.label(Component.translatable("gtceu.creative.tank.tpc")))
                .child(UIComponents.switchComponent((clickData, value) -> setWorkingEnabled(value))
                        .texture(
                                UITextures.group(GuiTextures.VANILLA_BUTTON,
                                        UITextures.text(Component.translatable("gtceu.creative.activity.off"))),
                                UITextures.group(GuiTextures.VANILLA_BUTTON,
                                        UITextures.text(Component.translatable("gtceu.creative.activity.on"))))
                        .pressed(isWorkingEnabled())
                        .sizing(Sizing.fixed(162), Sizing.fixed(20)));

        return group;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private class InfiniteCache extends FluidCache {

        public InfiniteCache(MetaMachine holder) {
            super(holder);
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return stored;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!stored.isEmpty() && stored.isFluidEqual(resource)) return resource.getAmount();
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (!stored.isEmpty()) return new FluidStack(stored, mBPerCycle);
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!stored.isEmpty() && stored.isFluidEqual(resource)) return new FluidStack(resource, mBPerCycle);
            return FluidStack.EMPTY;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return true;
        }

        @Override
        public int getTankCapacity(int tank) {
            return 1000;
        }
    }
}
