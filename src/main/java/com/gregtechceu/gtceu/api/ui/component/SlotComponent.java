package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.util.pond.UISlotExtension;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.SlotAccessor;
import lombok.Getter;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.lwjgl.opengl.GL11;

public class SlotComponent extends BaseUIComponent {
    private final BaseContainerScreen<?, ?> screen;
    @Getter
    protected final Slot slot;
    protected boolean didDraw = false;
    protected SlotComponent(BaseContainerScreen<?, ?> screen, IItemHandlerModifiable itemHandler, int index) {
        this.screen = screen;
        this.slot = new SlotItemHandler(itemHandler, index, x, y);
    }

    @Override
    public void draw(UIGuiGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        this.didDraw = true;

        int[] scissor = new int[4];
        GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissor);

        ((UISlotExtension) this.slot).gtceu$setScissorArea(PositionedRectangle.of(
                scissor[0], scissor[1], scissor[2], scissor[3]));
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
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        return super.shouldDrawTooltip(mouseX, mouseY);
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
    public void updateX(int x) {
        super.updateX(x);
        ((SlotAccessor) this.slot).gtceu$setX(x);
    }

    @Override
    public void updateY(int y) {
        super.updateY(y);
        ((SlotAccessor) this.slot).gtceu$setY(y);
    }
}
