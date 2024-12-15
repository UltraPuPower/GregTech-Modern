package com.gregtechceu.gtceu.integration.xei.widgets;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.GridLayout;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;

import net.minecraftforge.items.ItemStackHandler;

public class GTProgrammedCircuitComponent extends UIComponentGroup {

    public GTProgrammedCircuitComponent() {
        super(Sizing.fixed(150), Sizing.fixed(80));
        setRecipe();
    }

    public void setRecipe() {
        child(UIComponents.texture(GuiTextures.SLOT)
                .positioning(Positioning.absolute(39, 0))
                .sizing(Sizing.fixed(36)));

        ItemStackHandler handler = new CustomItemStackHandler(32);
        GridLayout grid = UIContainers.grid(Sizing.content(3), Sizing.content(0), 8, 4);
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 8; i++) {
                handler.setStackInSlot((i + j * 8), IntCircuitBehaviour.stack(1 + (i + j * 8)));
                grid.child(UIComponents.slot(handler, (i + j * 8))
                                .canInsert(false)
                                .canExtract(false)
                                .ingredientIO((i + j * 8 == 31 ? IO.OUT : IO.BOTH)),
                        i, i);
            }
        }
        child(grid);
    }
}
