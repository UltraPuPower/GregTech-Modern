package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.container.*;
import com.gregtechceu.gtceu.api.ui.core.*;

import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.AbstractContainerMenuAccessor;
import net.minecraft.world.entity.player.Inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.w3c.dom.Element;

import java.util.List;

public class PlayerInventoryComponent extends UIComponentGroup {

    protected PlayerInventoryComponent(Inventory inventory, UITexture slotTexture) {
        super(Sizing.fixed(162), Sizing.fixed(76));
        setByInventory(inventory, slotTexture);
        this.allowOverflow(true);
    }

    protected PlayerInventoryComponent() {
        super(Sizing.fixed(162), Sizing.fixed(76));
    }

    public PlayerInventoryComponent setByInventory(Inventory inventory, UITexture slotTexture) {
        GridLayout grid = UIContainers.grid(Sizing.content(), Sizing.content(), 3, 9);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                grid.child(UIComponents.slot(inventory, y + x * 9 + 9)
                                .backgroundTexture(slotTexture)
                                .positioning(Positioning.absolute(x * 18, y * 18)),
                        x, y);
            }
        }
        this.child(grid);

        var grid2 = UIContainers.grid(Sizing.content(), Sizing.content(), 1, 9);
        for (int x = 0; x < 9; x++) {
            grid.child(UIComponents.slot(inventory, x)
                            .backgroundTexture(slotTexture)
                            .positioning(Positioning.absolute(x * 18, y * 18)),
                    0, x);

            StackLayout layout = UIContainers.stack(Sizing.fixed(18), Sizing.fixed(18));
            layout.children(List.of(
                    UIComponents.slot(inventory, x)
                            .positioning(Positioning.absolute(x * 18, 58)),
                    UIComponents.texture(slotTexture, 18, 18)
                            .positioning(Positioning.absolute(x * 18, 58))
                            .sizing(Sizing.fixed(18))));
            grid2.child(layout, x, 0);
        }
        this.child(grid2);
        return this;
    }

    public static PlayerInventoryComponent parse(Element element) {
        return new PlayerInventoryComponent();
    }

}
