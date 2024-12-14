package com.gregtechceu.gtceu.integration.ae2.gui.widget;

import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEItemConfigSlotComponent;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlot;

import appeng.api.stacks.GenericStack;

/**
 * @author GlodBlock
 * @Description Display {@link net.minecraft.world.item.ItemStack} config
 * @date 2023/4/22-1:02
 */
public class AEItemConfigComponent extends ConfigComponent {

    private final ExportOnlyAEItemList itemList;

    public AEItemConfigComponent(int x, int y, ExportOnlyAEItemList list) {
        super(x, y, list.getInventory(), list.isStocking());
        this.itemList = list;
    }

    @Override
    void init() {
        int line;
        this.displayList = new IConfigurableSlot[this.config.length];
        this.cached = new IConfigurableSlot[this.config.length];
        for (int index = 0; index < this.config.length; index++) {
            this.displayList[index] = new ExportOnlyAEItemSlot();
            this.cached[index] = new ExportOnlyAEItemSlot();
            line = index / 8;
            this.addWidget(new AEItemConfigSlotComponent((index - line * 8) * 18, line * (18 * 2 + 2), this, index));
        }
    }

    public boolean hasStackInConfig(GenericStack stack) {
        return itemList.hasStackInConfig(stack, true);
    }

    public boolean isAutoPull() {
        return itemList.isAutoPull();
    }
}
