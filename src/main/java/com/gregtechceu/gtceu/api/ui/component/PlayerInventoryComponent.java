package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.core.*;

import net.minecraft.world.entity.player.Inventory;

import org.w3c.dom.Element;

public class PlayerInventoryComponent extends FlowLayout {

    protected PlayerInventoryComponent(Inventory inventory) {
        super(Sizing.fixed(162), Sizing.fixed(76), Algorithm.VERTICAL);
        setInventory(inventory);
    }

    protected PlayerInventoryComponent() {
        super(Sizing.fixed(162), Sizing.fixed(76), Algorithm.VERTICAL);
    }

    public PlayerInventoryComponent setInventory(Inventory inventory) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.child(UIComponents.slot(inventory, x + y * 9 + 9)
                        .positioning(Positioning.absolute(x * 18 + 1, y * 18 + 1)))
                        .child(UIComponents.texture(GuiTextures.SLOT.imageLocation, 0, 0, 18, 18, 18, 18)
                                .positioning(Positioning.absolute((18 * x), (18 * y))));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.child(UIComponents.slot(inventory, x)
                    .positioning(Positioning.absolute(x * 18 + 1, 59)))
                    .child(UIComponents.texture(GuiTextures.SLOT.imageLocation, 0, 0, 18, 18, 18, 18)
                            .positioning(Positioning.absolute((18 * x), 58)));
        }
        return this;
    }

    public static PlayerInventoryComponent parse(Element element) {
        return new PlayerInventoryComponent();
    }
}
