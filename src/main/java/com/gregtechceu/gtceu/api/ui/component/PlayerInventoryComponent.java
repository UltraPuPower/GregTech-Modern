package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.container.*;
import com.gregtechceu.gtceu.api.ui.core.*;

import com.gregtechceu.gtceu.core.mixins.ui.accessor.AbstractContainerMenuAccessor;
import net.minecraft.world.entity.player.Inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.w3c.dom.Element;

import java.util.List;

public class PlayerInventoryComponent extends UIComponentGroup {

    protected PlayerInventoryComponent(Inventory inventory) {
        super(Sizing.fixed(162), Sizing.fixed(76));
        setByInventory(inventory);
        this.allowOverflow(true);
    }

    protected PlayerInventoryComponent(AbstractContainerMenu menu, int startSlotIndex) {
        super(Sizing.fixed(162), Sizing.fixed(76));
        setByMenu(menu, startSlotIndex);
        this.allowOverflow(true);
    }

    protected PlayerInventoryComponent() {
        super(Sizing.fixed(162), Sizing.fixed(76));
    }

    public PlayerInventoryComponent setByInventory(Inventory inventory) {
        GridLayout grid = UIContainers.grid(Sizing.content(), Sizing.content(), 3, 9);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                StackLayout layout = UIContainers.stack(Sizing.fixed(18), Sizing.fixed(18));
                layout.children(List.of(UIComponents.slot(inventory, x + x * 9 + 9),
                        UIComponents.texture(GuiTextures.SLOT, 18, 18)))
                        .positioning(Positioning.absolute(x * 18, y * 18));
                grid.child(layout, x, y);
            }
        }
        this.child(grid);

        var grid2 = UIContainers.grid(Sizing.content(), Sizing.content(), 1, 9);
        for (int x = 0; x < 9; x++) {
            StackLayout layout = UIContainers.stack(Sizing.fixed(18), Sizing.fixed(18));
            layout.children(List.of(
                            UIComponents.slot(inventory, x)
                                    .positioning(Positioning.absolute(x * 18, 58)),
                    UIComponents.texture(GuiTextures.SLOT, 18, 18)
                            .positioning(Positioning.absolute(x * 18, 58))
                            .sizing(Sizing.fixed(18))));
            grid2.child(layout, x, 0);
        }
        this.child(grid2);
        return this;
    }

    public PlayerInventoryComponent setByMenu(AbstractContainerMenu menu, int startSlotIndex) {
        GridLayout grid = UIContainers.grid(Sizing.content(), Sizing.content(), 3, 9);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 9; y++) {
                StackLayout layout = UIContainers.stack(Sizing.fixed(18), Sizing.fixed(18));
                layout.children(List.of(UIComponents.slot(menu.getSlot(y + x * 9 + startSlotIndex)),
                        UIComponents.texture(GuiTextures.SLOT, 18, 18)
                                .sizing(Sizing.fixed(18))));
                grid.child(layout, x, y);
            }
        }
        grid.positioning(Positioning.absolute(0, 0));
        this.child(grid);

        var grid2 = UIContainers.grid(Sizing.content(), Sizing.content(), 1, 9);
        for (int y = 0; y < 9; y++) {
            StackLayout layout = UIContainers.stack(Sizing.fixed(18), Sizing.fixed(18));
            layout.children(List.of(
                    UIComponents.slot(menu.getSlot(y + 27 + startSlotIndex)),
                    UIComponents.texture(GuiTextures.SLOT, 18, 18)
                            .sizing(Sizing.fixed(18))));
            grid2.child(layout, 0, y);
        }
        grid2.positioning(Positioning.absolute(0, 58));
        this.child(grid2);
        return this;
    }

    public static void addServerInventory(AbstractContainerMenu menu, Inventory inventory, int startX, int startY) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                ((AbstractContainerMenuAccessor) menu).gtceu$addSlot(new Slot(inventory, x + y * 9 + 9, 18 * x + startX, 18 * y + startY));
            }
        }

        for (int x = 0; x < 9; x++) {
            ((AbstractContainerMenuAccessor) menu).gtceu$addSlot(new Slot(inventory, x, 18 * x + startX, startY + 59));
        }
    }

    public static PlayerInventoryComponent parse(Element element) {
        return new PlayerInventoryComponent();
    }
}
