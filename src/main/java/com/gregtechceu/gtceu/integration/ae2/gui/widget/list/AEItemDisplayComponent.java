package com.gregtechceu.gtceu.integration.ae2.gui.widget.list;

import com.gregtechceu.gtceu.api.ui.GuiTextures;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.component.ItemComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

import static com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEConfigSlotComponent.drawSelectionOverlay;
import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawItemStack;
import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawText;

/**
 * @author GlodBlock
 * @apiNote Display a certain {@link appeng.api.stacks.GenericStack} element.
 * @date 2023/4/19-21:23
 */
public class AEItemDisplayComponent extends BaseUIComponent {

    private final AEListGridComponent gridWidget;
    private final int index;

    public AEItemDisplayComponent(AEListGridComponent gridWidget, int index) {
        this.gridWidget = gridWidget;
        this.index = index;
        this.sizing(Sizing.fixed(18));
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        GenericStack item = this.gridWidget.getAt(this.index);
        if (item != null) {
            tooltip(ItemComponent.tooltipFromItem(GenericStack.wrapInItemStack(item),
                    Minecraft.getInstance().player, null));
        }
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        GenericStack item = this.gridWidget.getAt(this.index);
        GuiTextures.SLOT.draw(graphics, mouseX, mouseY, x(), y(), 18, 18);
        GuiTextures.NUMBER_BACKGROUND.draw(graphics, mouseX, mouseY, x() + 18, y(), 140, 18);
        int stackX = x() + 1;
        int stackY = y() + 1;
        if (item != null) {
            ItemStack realStack = item.what() instanceof AEItemKey key ? new ItemStack(key.getItem()) : ItemStack.EMPTY;
            drawItemStack(graphics, realStack, stackX, stackY, -1, null);
            String amountStr = String.format("x%,d", item.amount());
            drawText(graphics, amountStr, stackX + 20, stackY + 5, 1, 0xFFFFFFFF);
        }
        if (isMouseOverElement(mouseX, mouseY)) {
            drawSelectionOverlay(graphics, stackX, stackY, 16, 16);
        }
    }
}
