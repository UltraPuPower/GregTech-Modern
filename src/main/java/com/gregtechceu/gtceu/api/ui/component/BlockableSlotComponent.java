package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

/**
 * Basically just your normal SlotComponent, but can render the slot as "grayed-out" with a Supplier value.
 */
@Accessors(fluent = true, chain = true)
public class BlockableSlotComponent extends SlotComponent {

    private static final int OVERLAY_COLOR = 0x80404040;

    @Setter
    protected BooleanSupplier isBlocked = () -> false;

    protected Boolean actualCanInsert;
    protected Boolean actualCanExtract;

    // TODO make protected
    public BlockableSlotComponent(int index) {
        super(index);
    }

    public BlockableSlotComponent(IItemHandlerModifiable itemHandler, int index) {
        super(itemHandler, index);
    }

    public BlockableSlotComponent(Container container, int index) {
        super(container, index);
    }

    public BlockableSlotComponent(Slot slot) {
        super(slot);
    }

    @Override
    public SlotComponent canInsert(@Nullable Boolean canInsert) {
        this.actualCanInsert = canInsert;
        return super.canInsert(canInsert);
    }

    @Override
    public SlotComponent canExtract(@Nullable Boolean canExtract) {
        this.actualCanExtract = canExtract;
        return super.canExtract(canExtract);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        if (isBlocked.getAsBoolean()) {
            super.canInsert(false);
            super.canExtract(false);
        } else {
            super.canInsert(actualCanInsert);
            super.canExtract(actualCanExtract);
        }
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        if (isBlocked.getAsBoolean()) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            graphics.fill(x() + 1, y() + 1, x() + 1 + width() - 2,
                    y() + 1 + height() - 2, OVERLAY_COLOR);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
        }
    }

    @Override
    public boolean isMouseOverElement(double mouseX, double mouseY) {
        // prevent slot removal and hover highlighting when slot is blocked
        return super.isMouseOverElement(mouseX, mouseY) && !isBlocked.getAsBoolean();
    }
}
