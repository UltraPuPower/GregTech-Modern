package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.UIContainer;
import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.pond.UISlotExtension;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.SlotAccessor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Element;

import javax.annotation.Nullable;

public class SlotComponent extends BaseUIComponent {

    @Getter
    @Setter
    private int index;
    @Getter
    @Setter
    protected String handlerName;
    @Getter
    @Setter
    protected MutableSlotWrapper slot;

    protected boolean didDraw = false;

    protected SlotComponent(int index) {
        this.index = index;
        this.slot = new MutableSlotWrapper(new UIContainer.EmptySlotPlaceholder());
    }

    protected SlotComponent(IItemHandlerModifiable itemHandler, int index) {
        this.index = index;
        this.slot = new MutableSlotWrapper(new SlotItemHandler(itemHandler, index, x, y));
    }

    protected SlotComponent(Slot slot) {
        this.index = slot.getSlotIndex();
        this.slot = new MutableSlotWrapper(slot);
    }

    protected SlotComponent(Container container, int index) {
        this.index = index;
        this.slot = new MutableSlotWrapper(new Slot(container, index, x, y));
    }

    @Override
    public void draw(UIGuiGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        this.didDraw = true;

        int[] scissor = new int[4];
        GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissor);

        ((UISlotExtension) this.slot).gtceu$setScissorArea(PositionedRectangle.of(
                scissor[0], scissor[1], scissor[2], scissor[3]));
    }

    public static SlotComponent parse(Element element) {
        UIParsing.expectAttributes(element, "index");
        UIParsing.expectAttributes(element, "name");
        int index = UIParsing.parseUnsignedInt(element.getAttributeNode("index"));
        String name = element.getAttribute("name");

        SlotComponent component = new SlotComponent(index);
        component.setHandlerName(name);
        return component;
    }

    protected void updateSlot(Slot slot) {
        this.slot.setInner(slot);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);

        ((UISlotExtension) this.slot).gtceu$setDisabledOverride(!this.didDraw);

        this.didDraw = false;
    }

    @Override
    public void drawTooltip(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (!this.slot.hasItem()) {
            super.drawTooltip(graphics, mouseX, mouseY, partialTicks, delta);
        }
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 16;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 16;
    }

    @Override
    public SlotComponent x(int x) {
        super.x(x);
        ((SlotAccessor) this.slot).gtceu$setX(x);
        return this;
    }

    @Override
    public SlotComponent y(int y) {
        super.y(y);
        ((SlotAccessor) this.slot).gtceu$setY(y);
        return this;
    }

    @Setter
    @Getter
    public static class MutableSlotWrapper extends Slot {

        private Slot inner;

        public MutableSlotWrapper(Slot inner) {
            super(inner.container, inner.getSlotIndex(), inner.x, inner.y);
            this.inner = inner;
        }

        @Override
        public int getSlotIndex() {
            return inner.getSlotIndex();
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return inner.isActive();
        }

        @Override
        @NotNull
        public ItemStack getItem() {
            return inner.getItem();
        }

        @Override
        public boolean hasItem() {
            return inner.hasItem();
        }

        @Override
        public void setByPlayer(@NotNull ItemStack stack) {
            inner.setByPlayer(stack);
        }

        /**
         * Helper method to put a stack in the slot.
         */
        @Override
        public void set(@NotNull ItemStack stack) {
            inner.set(stack);
        }

        @Override
        public void setChanged() {
            inner.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return inner.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return inner.getMaxStackSize(stack);
        }

        @Nullable
        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return inner.getNoItemIcon();
        }

        /**
         * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
         * stack.
         */
        @Override
        @NotNull
        public ItemStack remove(int amount) {
            return inner.remove(amount);
        }

        /**
         * Return whether this slot's stack can be taken from this slot.
         */
        @Override
        public boolean mayPickup(@NotNull Player player) {
            return inner.mayPickup(player);
        }

        @Override
        public boolean isActive() {
            return inner.isActive();
        }

        @SuppressWarnings("unused") // it actually overrides an accessor mixin's method.
        public void gtceu$setX(int x) {
            this.x = x;
            ((SlotAccessor) inner).gtceu$setX(x);
        }

        @SuppressWarnings("unused") // it actually overrides an accessor mixin's method.
        public void gtceu$setY(int y) {
            this.y = y;
            ((SlotAccessor) inner).gtceu$setY(y);
        }

        @SuppressWarnings("unused") // it actually overrides an accessor mixin's method.
        public void gtceu$setSlotIndex(int index) {
            this.index = index;
            inner.index = index;
        }
    }
}
