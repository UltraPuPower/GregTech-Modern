package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import net.minecraft.world.entity.player.Inventory;

public class PlayerInventoryComponent extends FlowLayout {

    protected PlayerInventoryComponent(Inventory inventory) {
        super(Sizing.fixed(162), Sizing.fixed(76), Algorithm.VERTICAL);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.child(UIComponents.slot(inventory, x + y * 9 + 9)
                        .positioning(Positioning.absolute(x * 18, y * 18)));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.child(UIComponents.slot(inventory, x)
                    .positioning(Positioning.absolute(x * 18, 58)));
        }
    }

}
