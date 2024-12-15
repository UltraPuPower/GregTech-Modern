package com.gregtechceu.gtceu.integration.ae2.gui.widget;

import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEItemConfigSlotComponent;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlot;

import appeng.api.stacks.GenericStack;

/**
 * @author GlodBlock
 * @apiNote Display {@link net.minecraft.world.item.ItemStack} config
 * @date 2023/4/22-1:02
 */
public class AEItemConfigComponent extends ConfigComponent {

    private final ExportOnlyAEItemList itemList;

    public AEItemConfigComponent(ExportOnlyAEItemList list) {
        super(list.getInventory(), list.isStocking());
        this.itemList = list;
    }

    @Override
    public void init() {
        int line;
        this.displayList = new IConfigurableSlot[this.config.length];
        this.cached = new IConfigurableSlot[this.config.length];
        for (int index = 0; index < this.config.length; index++) {
            this.displayList[index] = new ExportOnlyAEItemSlot();
            this.cached[index] = new ExportOnlyAEItemSlot();
            line = index / 8;
            this.child(new AEItemConfigSlotComponent(this, index)
                    // TODO use layout?
                    .positioning(Positioning.absolute((index - line * 8) * 18, line * (18 * 2 + 2))));
        }
    }

    public boolean hasStackInConfig(GenericStack stack) {
        return itemList.hasStackInConfig(stack, true);
    }

    public boolean isAutoPull() {
        return itemList.isAutoPull();
    }
}
