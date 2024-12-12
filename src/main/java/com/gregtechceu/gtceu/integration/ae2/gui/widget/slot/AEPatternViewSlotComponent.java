package com.gregtechceu.gtceu.integration.ae2.gui.widget.slot;

import com.gregtechceu.gtceu.api.ui.component.SlotComponent;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import net.minecraft.world.Container;
import net.minecraftforge.items.IItemHandlerModifiable;

public class AEPatternViewSlotComponent extends SlotComponent {

    protected UITexture backgroundTexture;
    protected UITexture occupiedTexture;

    public AEPatternViewSlotComponent(
                                   Container inventory,
                                   int slotIndex,
                                   boolean canTakeItems,
                                   boolean canPutItems) {
        super(inventory, slotIndex);
        this.canInsertOverride(canPutItems);
        this.canExtractOverride(canTakeItems);
    }

    public AEPatternViewSlotComponent(IItemHandlerModifiable itemHandler, int slotIndex) {
        super(itemHandler, slotIndex);
    }

    public AEPatternViewSlotComponent occupiedTexture(UITexture... occupiedTexture) {
        this.occupiedTexture = occupiedTexture.length > 1 ? UITextures.group(occupiedTexture) : occupiedTexture[0];
        return this;
    }

    public AEPatternViewSlotComponent backgroundTexture(UITexture... backgroundTexture) {
        this.backgroundTexture = backgroundTexture.length > 1 ?
                UITextures.group(backgroundTexture) : backgroundTexture[0];
        return this;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        if (occupiedTexture != null) {
            occupiedTexture.updateTick();
        }
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        if (slot.hasItem()) {
            if (occupiedTexture != null) {
                occupiedTexture.draw(graphics, mouseX, mouseY, x(), y(), width(), height());
            }
        } else {
            if (backgroundTexture != null) {
                backgroundTexture.draw(graphics, mouseX, mouseY, x(), y(), width(), height());
            }
        }
    }
}
