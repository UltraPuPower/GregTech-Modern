package com.gregtechceu.gtceu.api.ui;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.ui.component.SlotComponent;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.factory.UIFactory;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.AbstractContainerMenuAccessor;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.SlotAccessor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class UIContainer extends AbstractContainerMenu {

    public final static MenuType<UIContainer> MENU_TYPE = GTRegistries.register(BuiltInRegistries.MENU,
            GTCEu.id("ui_container"), IForgeMenuType.create(UIContainer::new));

    @Getter
    private final HashMap<Slot, SlotComponent> slotMap = new LinkedHashMap<>();

    private Inventory playerInventory;
    @Getter
    @Setter
    private UIAdapter<RootContainer> adapter;

    protected UIContainer(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    // should work as long as someone doesn't do dumb things
    // (e.g. use a different parent component for their factory and container)
    public UIContainer(int containerId, Inventory playerInventory, @Nullable FriendlyByteBuf data) {
        super(MENU_TYPE, containerId);
        this.playerInventory = playerInventory;

        if (data != null) {
            ResourceLocation uiFactoryId = data.readResourceLocation();
            UIFactory<?> factory = UIFactory.FACTORIES.get(uiFactoryId);

            this.adapter = factory.initClientUI(data);
        }
        init();
    }

    public UIContainer(int containerId, Inventory playerInventory, UIAdapter<RootContainer> adapter) {
        super(MENU_TYPE, containerId);
        this.playerInventory = playerInventory;
        this.adapter = adapter;
        init();
    }

    /**
     * Initialize all container data like slots here.
     * Separate method from the constructors to avoid duplicate code.
     */
    public void init() {
        // clear all old data before adding any new ones
        clear();

        // don't init anything if we don't have a valid adapter.
        if (adapter == null) {
            return;
        }
        addAllSlots();
    }

    public void clear() {
        this.slots.clear();
        ((AbstractContainerMenuAccessor) this).gtceu$getLastSlots().clear();
        ((AbstractContainerMenuAccessor) this).gtceu$getRemoteSlots().clear();
    }

    public void addAllSlots() {
        var root = adapter.rootComponent;
        root.forEachDescendant(child -> {
            if (child instanceof SlotComponent slot) {
                addSlotComponent(slot.getSlot(), slot);
            }
        });
    }

    //WARNING! WIDGET CHANGES SHOULD BE *STRICTLY* SYNCHRONIZED BETWEEN SERVER AND CLIENT,
    //OTHERWISE ID MISMATCH CAN HAPPEN BETWEEN ASSIGNED SLOTS!
    @Nonnull
    public Slot addSlot(@Nonnull Slot slot) {
        var emptySlotIndex = this.slots.stream()
                .filter(it -> it instanceof EmptySlotPlaceholder)
                .mapToInt(Slot::getSlotIndex)
                .findFirst();
        if (emptySlotIndex.isPresent()) {
            ((SlotAccessor) slot).gtceu$setSlotIndex(emptySlotIndex.getAsInt());
            this.slots.set(slot.getSlotIndex(), slot);
            ((AbstractContainerMenuAccessor)this).gtceu$getLastSlots().set(slot.getSlotIndex(), ItemStack.EMPTY);
            ((AbstractContainerMenuAccessor)this).gtceu$getRemoteSlots().set(slot.getSlotIndex(), ItemStack.EMPTY);
            return slot;
        }
        return super.addSlot(slot);
    }

    //WARNING! WIDGET CHANGES SHOULD BE *STRICTLY* SYNCHRONIZED BETWEEN SERVER AND CLIENT,
    //OTHERWISE ID MISMATCH CAN HAPPEN BETWEEN ASSIGNED SLOTS!
    public void removeSlot(Slot slot) {
        if (this.slotMap.remove(slot) == null) {
            GTCEu.LOGGER.error("removed nonexistent slot {}", slot);
            return;
        }

        //replace removed slot with empty placeholder to avoid list index shift
        EmptySlotPlaceholder emptySlotPlaceholder = new EmptySlotPlaceholder();
        emptySlotPlaceholder.index = slot.index;
        this.slots.set(slot.getSlotIndex(), emptySlotPlaceholder);
        ((AbstractContainerMenuAccessor)this).gtceu$getLastSlots().set(slot.getSlotIndex(), ItemStack.EMPTY);
        ((AbstractContainerMenuAccessor)this).gtceu$getRemoteSlots().set(slot.getSlotIndex(), ItemStack.EMPTY);
    }

    //WARNING! WIDGET CHANGES SHOULD BE *STRICTLY* SYNCHRONIZED BETWEEN SERVER AND CLIENT,
    //OTHERWISE ID MISMATCH CAN HAPPEN BETWEEN ASSIGNED SLOTS!
    public void addSlotComponent(Slot slot, SlotComponent slotComponent) {
        if (this.slotMap.containsKey(slot)) {
            GTCEu.LOGGER.error("duplicated slot {}, {}", slot, slotComponent);
        }
        this.slotMap.put(slot, slotComponent);
        this.addSlot(slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        if (playerInventory.player.level().isClientSide) {
            return ItemStack.EMPTY;
        }

        final Slot clickedSlot = this.slots.get(i);
        boolean playerSide = isPlayerSideSlot(clickedSlot);

        //if(clickedSlot.isActive()) // todo disabled slots

        if (clickedSlot.hasItem()) {
            ItemStack stack = clickedSlot.getItem();

            final List<Slot> selectedSlots = new ArrayList<>();

            if (playerSide) {
                for (Slot c : this.slots) {
                    if (!isPlayerSideSlot(c) && c.mayPlace(stack)) {
                        selectedSlots.add(c);
                    }
                }
            } else {
                for (Slot c : this.slots) {
                    if (isPlayerSideSlot(c) && c.mayPlace(stack)) {
                        selectedSlots.add(c);
                    }
                }
            }

            if (!stack.isEmpty()) {
                for (Slot d : selectedSlots) {
                    if (d.mayPlace(stack) && d.hasItem() && movedFullStack(clickedSlot, stack, d)) {
                        return ItemStack.EMPTY;
                    }
                }

                for (Slot d : selectedSlots) {
                    if (d.mayPlace(stack)) {
                        if (d.hasItem()) {
                            if (movedFullStack(clickedSlot, stack, d)) {
                                return ItemStack.EMPTY;
                            }
                        } else {
                            int maxSize = stack.getMaxStackSize();
                            if (maxSize > d.getMaxStackSize()) {
                                maxSize = d.getMaxStackSize();
                            }

                            final ItemStack tmp = stack.copy();
                            if (tmp.getCount() > maxSize) {
                                tmp.setCount(maxSize);
                            }

                            stack.setCount(stack.getCount() - tmp.getCount());
                            d.set(tmp);
                            if (stack.getCount() <= 0) {
                                clickedSlot.set(ItemStack.EMPTY);
                                d.setChanged();

                                broadcastChanges();
                                return ItemStack.EMPTY;
                            } else {
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
        return s.container == this.playerInventory;
    }

    private boolean movedFullStack(Slot clickedSlot, ItemStack stack, Slot dest) {
        final ItemStack t = dest.getItem().copy();

        if (ItemStack.isSameItemSameTags(t, stack)) {
            int maxSize = t.getMaxStackSize();
            if (maxSize > dest.getMaxStackSize()) {
                maxSize = dest.getMaxStackSize();
            }

            int placeable = maxSize - t.getCount();
            if (placeable > 0) {
                if (stack.getCount() < placeable) {
                    placeable = stack.getCount();
                }

                t.setCount(t.getCount() + placeable);
                stack.setCount(stack.getCount() - placeable);

                dest.set(t);

                if (stack.getCount() <= 0) {
                    clickedSlot.set(ItemStack.EMPTY);
                    dest.setChanged();

                    broadcastChanges();
                    return true;
                } else {
                    broadcastChanges();
                }
            }
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static class EmptySlotPlaceholder extends Slot {

        public static final Container EMPTY_INVENTORY = new SimpleContainer(0);

        public EmptySlotPlaceholder() {
            super(EMPTY_INVENTORY, 0, -100000, -100000);
        }

        @Nonnull
        @Override
        public ItemStack getItem() {
            return ItemStack.EMPTY;
        }

        @Override
        public void set(@Nonnull ItemStack stack) {}

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(@Nonnull Player playerIn) {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }

    public static void initType() {

    }
}
