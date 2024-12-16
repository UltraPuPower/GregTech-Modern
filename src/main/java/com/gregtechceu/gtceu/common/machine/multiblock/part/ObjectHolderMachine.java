package com.gregtechceu.gtceu.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.capability.IObjectHolder;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IDataItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.BlockableSlotComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.fancy.FancyMachineUIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ObjectHolderMachine extends MultiblockPartMachine implements IObjectHolder, IMachineLife {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ObjectHolderMachine.class,
            MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    // purposefully not exposed to automation or capabilities
    @Persisted
    private final ObjectHolderHandler heldItems;
    @Getter
    @Setter
    @Persisted
    @DescSynced
    private boolean isLocked;

    public ObjectHolderMachine(IMachineBlockEntity holder) {
        super(holder);
        heldItems = new ObjectHolderHandler(this);
    }

    @Override
    public @NotNull ItemStack getHeldItem(boolean remove) {
        return getHeldItem(0, remove);
    }

    @Override
    public void setHeldItem(@NotNull ItemStack heldItem) {
        heldItems.setStackInSlot(0, heldItem);
    }

    @Override
    public @NotNull ItemStack getDataItem(boolean remove) {
        return getHeldItem(1, remove);
    }

    @Override
    public void setDataItem(@NotNull ItemStack dataItem) {
        heldItems.setStackInSlot(1, dataItem);
    }

    @Override
    public @NotNull NotifiableItemStackHandler getAsHandler() {
        return heldItems;
    }

    @NotNull
    private ItemStack getHeldItem(int slot, boolean remove) {
        ItemStack stackInSlot = heldItems.getStackInSlot(slot);
        if (remove && stackInSlot != ItemStack.EMPTY) {
            heldItems.setStackInSlot(slot, ItemStack.EMPTY);
        }
        return stackInSlot;
    }

    @Override
    public void onMachineRemoved() {
        clearInventory(this.heldItems.storage);
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        super.loadServerUI(player, menu, holder);
    }

    @Override
    public ParentUIComponent createBaseUIComponent(FancyMachineUIComponent component) {
        return UIContainers.group(Sizing.content(), Sizing.content())
                .child(UIComponents.texture(GuiTextures.PROGRESS_BAR_RESEARCH_STATION_BASE)
                        .positioning(Positioning.absolute(46, 15))
                        .sizing(Sizing.fixed(84), Sizing.fixed(60)))
                .child(new BlockableSlotComponent(heldItems, 0)
                        .isBlocked(this::isLocked)
                        .backgroundTexture(UITextures.group(GuiTextures.SLOT, GuiTextures.RESEARCH_STATION_OVERLAY))
                        .positioning(Positioning.absolute(79, 36)))
                .child(new BlockableSlotComponent(heldItems, 1)
                        .isBlocked(this::isLocked)
                        .backgroundTexture(UITextures.group(GuiTextures.SLOT, GuiTextures.DATA_ORB_OVERLAY))
                        .positioning(Positioning.absolute(15, 36)));
    }

    @Override
    public void setFrontFacing(Direction frontFacing) {
        super.setFrontFacing(frontFacing);
        var controllers = getControllers();
        for (var controller : controllers) {
            if (controller != null && controller.isFormed()) {
                controller.checkPatternWithLock();
            }
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private class ObjectHolderHandler extends NotifiableItemStackHandler {

        public ObjectHolderHandler(MetaMachine metaTileEntity) {
            super(metaTileEntity, 2, IO.IN, IO.BOTH, size -> new CustomItemStackHandler(size) {

                @Override
                public int getSlotLimit(int slot) {
                    return 1;
                }
            });
        }

        // only allow a single item, no stack size
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        // prevent extracting the item while running
        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!isLocked()) {
                return super.extractItem(slot, amount, simulate);
            }
            return ItemStack.EMPTY;
        }

        // only allow data items in the second slot
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (stack.isEmpty()) {
                return true;
            }

            boolean isDataItem = false;
            if (stack.getItem() instanceof IComponentItem metaItem) {
                for (IItemComponent behaviour : metaItem.getComponents()) {
                    if (behaviour instanceof IDataItem) {
                        isDataItem = true;
                        break;
                    }
                }
            }

            if (slot == 0 && !isDataItem) {
                return true;
            } else return slot == 1 && isDataItem;
        }
    }
}
