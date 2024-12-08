package com.gregtechceu.gtceu.api.ui;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.component.ItemComponent;
import com.gregtechceu.gtceu.api.ui.component.SlotComponent;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UIContainer extends AbstractContainerMenu {

    public final static MenuType<UIContainer> DEFAULT_TYPE = new MenuType<>(UIContainer::new, FeatureFlags.DEFAULT_FLAGS);

    private Inventory playerInv;
    @Setter
    private UIAdapter<?> adapter;

    protected UIContainer(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    public UIContainer(int containerId, Inventory playerInv) {
        super(DEFAULT_TYPE, containerId);
        this.playerInv = playerInv;
    }

    public UIContainer(int containerId, Inventory playerInv, UIAdapter<?> adapter) {
        super(DEFAULT_TYPE, containerId);
        this.playerInv = playerInv;
        this.adapter = adapter;
    }

    public void addAllSlots() {
        var root = adapter.rootComponent;
        root.forEachDescendant(child -> {
            if(child instanceof SlotComponent slot) {
                addSlot(slot.getSlot());
            };
        });
        int slotSize = this.slots.size();


        for(int i1 = 0; i1 < 3; i1++) {
            for(int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(playerInv, j1 + i1 * 9 + 9, 8 + j1 * 18, 103 + i1 * 18));
            }
        }

        for(int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(playerInv, i1, 8 + i1 * 18, 161));
        }



    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        if(playerInv.player.level().isClientSide) {
            return ItemStack.EMPTY;
        }

        final Slot clickedSlot = this.slots.get(i);
        boolean playerSide = isPlayerSideSlot(clickedSlot);

        //if(clickedSlot.isActive()) // todo disabled slots

        if(clickedSlot.hasItem()) {
            ItemStack stack = clickedSlot.getItem();

            final List<Slot> selectedSlots = new ArrayList<>();

            if(playerSide) {
                for(Slot c : this.slots) {
                    if(!isPlayerSideSlot(c) && c.mayPlace(stack)) {
                        selectedSlots.add(c);
                    }
                }
            } else {
                for(Slot c : this.slots) {
                    if(isPlayerSideSlot(c) && c.mayPlace(stack)) {
                        selectedSlots.add(c);
                    }
                }
            }

            if(!stack.isEmpty()) {
                for(Slot d : selectedSlots){
                    if(d.mayPlace(stack) && d.hasItem() && movedFullStack(clickedSlot, stack, d)) {
                        return ItemStack.EMPTY;
                    }
                }

                for(Slot d : selectedSlots) {
                    if(d.mayPlace(stack)) {
                        if(d.hasItem()) {
                            if(movedFullStack(clickedSlot, stack, d)) {
                                return ItemStack.EMPTY;
                            }
                        }
                        else {
                            int maxSize = stack.getMaxStackSize();
                            if (maxSize > d.getMaxStackSize()) {
                                maxSize = d.getMaxStackSize();
                            }

                            final ItemStack tmp = stack.copy();
                            if(tmp.getCount() > maxSize) {
                                tmp.setCount(maxSize);
                            }

                            stack.setCount( stack.getCount() - tmp.getCount());
                            d.set(tmp);
                            if(stack.getCount() <= 0) {
                                clickedSlot.set(ItemStack.EMPTY);
                                d.setChanged();

                                broadcastChanges();
                                return ItemStack.EMPTY;
                            }
                            else {
                                broadcastChanges();
                            }
                        }
                    }
                }
            }
            clickedSlot.set(!stack.isEmpty() ? stack : ItemStack.EMPTY);
        }
        broadcastChanges();
        return ItemStack.EMPTY;
    }

    private boolean isPlayerSideSlot(Slot s) {
        return s.container == this.playerInv;
    }

    private boolean movedFullStack(Slot clickedSlot, ItemStack stack, Slot dest) {
        final ItemStack t = dest.getItem().copy();

        if(ItemStack.isSameItemSameTags(t, stack)) {
            int maxSize = t.getMaxStackSize();
            if(maxSize > dest.getMaxStackSize()) {
                maxSize = dest.getMaxStackSize();
            }

            int placeable = maxSize - t.getCount();
            if(placeable > 0) {
                if(stack.getCount() < placeable) {
                    placeable = stack.getCount();
                }

                t.setCount(t.getCount() + placeable);
                stack.setCount(stack.getCount() - placeable);

                dest.set(t);

                if(stack.getCount() <=0) {
                    clickedSlot.set(ItemStack.EMPTY);
                    dest.setChanged();

                    broadcastChanges();
                    return true;
                }
                else {
                    broadcastChanges();
                }
            }
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void gtceu$attachToPlayer(Player player) {

    }

    @Override
    public void gtceu$readPropertySync(FriendlyByteBuf packet) {

    }

    @Override
    public void gtceu$handlePacket(FriendlyByteBuf packet, boolean clientbound) {

    }
}
