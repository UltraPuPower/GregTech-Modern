package com.gregtechceu.gtceu.common.machine.storage;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.PhantomSlotComponent;
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

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreativeChestMachine extends QuantumChestMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CreativeChestMachine.class,
            QuantumChestMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    @DropSaved
    private int itemsPerCycle = 1;
    @Getter
    @Persisted
    @DropSaved
    private int ticksPerCycle = 1;

    public CreativeChestMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.MAX, -1);
    }

    @Override
    protected ItemCache createCacheItemHandler(Object... args) {
        return new InfiniteCache(this);
    }

    protected void checkAutoOutput() {
        if (getOffsetTimer() % ticksPerCycle == 0) {
            if (isAutoOutputItems() && getOutputFacingItems() != null) {
                cache.exportToNearby(getOutputFacingItems());
            }
            updateAutoOutputSubscription();
        }
    }

    private InteractionResult updateStored(ItemStack item) {
        stored = item.copyWithCount(1);
        onItemChanged();
        return InteractionResult.SUCCESS;
    }

    private void setTicksPerCycle(String value) {
        if (value.isEmpty()) return;
        ticksPerCycle = Integer.parseInt(value);
        onItemChanged();
    }

    private void setItemsPerCycle(String value) {
        if (value.isEmpty()) return;
        itemsPerCycle = Integer.parseInt(value);
        onItemChanged();
    }

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                   BlockHitResult hit) {
        var heldItem = player.getItemInHand(hand);
        if (hit.getDirection() == getFrontFacing() && !isRemote()) {
            // Clear item if empty hand + shift-rclick
            if (heldItem.isEmpty() && player.isCrouching() && !stored.isEmpty()) {
                return updateStored(ItemStack.EMPTY);
            }

            // If held item can stack with stored item, delete held item
            if (!heldItem.isEmpty() && ItemHandlerHelper.canItemStacksStack(stored, heldItem)) {
                player.setItemInHand(hand, ItemStack.EMPTY);
                return InteractionResult.SUCCESS;
            } else if (!heldItem.isEmpty()) { // If held item is different than stored item, update stored item
                return updateStored(heldItem);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        // TODO implement
        // NEEDS MESSAGES FOR TICKS/ITEMS PER CYCLE & ACTIVE STATE, REMEMBER TO ADD THOSE!!
    }

    @Override
    public ParentUIComponent createBaseUIComponent(FancyMachineUIComponent component) {
        var group = UIContainers.verticalFlow(Sizing.fixed(176), Sizing.fixed(131));
        group.padding(Insets.both(7, 9));
        group.child(UIComponents.label(Component.translatable("gtceu.creative.chest.item")))
                .child(new PhantomSlotComponent(cache, 0)
                        .clearSlotOnRightClick(true)
                        .maxStackSize(1)
                        .backgroundTexture(GuiTextures.SLOT)
                        .changeListener(this::markDirty)
                        .positioning(Positioning.absolute(29, -3)))
                .child(UIContainers.verticalFlow(Sizing.fixed(154), Sizing.fixed(14))
                        .<FlowLayout>configure(c -> {
                            c.surface(Surface.UI_DISPLAY)
                                    .positioning(Positioning.absolute(0, 39));
                        }).child(UIComponents.textBox(Sizing.fixed(152))
                                .textSupplier(() -> String.valueOf(itemsPerCycle))
                                .<TextBoxComponent>configure(c -> {
                                    c.onChanged().subscribe(value -> {
                                        this.setItemsPerCycle(value);
                                    });
                                    c.setMaxLength(11);
                                })
                                .numbersOnly(1, Integer.MAX_VALUE)))
                .child(UIComponents.label(Component.translatable("gtceu.creative.chest.ipc")))
                .child(UIContainers.verticalFlow(Sizing.fixed(154), Sizing.fixed(14))
                        .<FlowLayout>configure(c -> {
                            c.surface(Surface.UI_DISPLAY)
                                    .positioning(Positioning.absolute(7, 85));
                        }).child(UIComponents.textBox(Sizing.fixed(152))
                                .textSupplier(() -> String.valueOf(ticksPerCycle))
                                .<TextBoxComponent>configure(c -> {
                                    c.onChanged().subscribe(value -> {
                                        this.setTicksPerCycle(value);
                                    });
                                    c.setMaxLength(11);
                                })
                                .numbersOnly(1, Integer.MAX_VALUE)))
                .child(UIComponents.label(Component.translatable("gtceu.creative.chest.tpc")))
                .child(UIComponents.switchComponent((clickData, value) -> setWorkingEnabled(value))
                        .texture(UITextures.group(GuiTextures.VANILLA_BUTTON, UITextures.text(Component.translatable(
                                "gtceu.creative.activity.off"))),
                                UITextures.group(GuiTextures.VANILLA_BUTTON, UITextures.text(Component.translatable(
                                        "gtceu.creative.activity.on"))))
                        .pressed(isWorkingEnabled())
                        .sizing(Sizing.fixed(162), Sizing.fixed(20)));
        return group;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private class InfiniteCache extends ItemCache {

        public InfiniteCache(MetaMachine holder) {
            super(holder);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return stored;
        }

        @Override
        public void setStackInSlot(int index, ItemStack stack) {
            updateStored(stack);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (!stored.isEmpty() && ItemStack.isSameItemSameTags(stored, stack)) return ItemStack.EMPTY;
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!stored.isEmpty()) return stored.copyWithCount(itemsPerCycle);
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }
}
