package com.gregtechceu.gtceu.api.machine.fancyconfigurator;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import com.gregtechceu.gtceu.api.ui.texture.UITexture;

import net.minecraft.network.chat.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Accessors(chain = true)
public class FancyInvConfigurator implements IFancyConfigurator {

    private final CustomItemStackHandler inventory;

    @Getter
    private final Component title;

    @Getter
    @Setter
    private List<Component> tooltips = Collections.emptyList();

    public FancyInvConfigurator(CustomItemStackHandler inventory, Component title) {
        this.inventory = inventory;
        this.title = title;
    }

    @Override
    public UITexture getIcon() {
        return GuiTextures.BUTTON_ITEM_OUTPUT;
    }

    @Override
    public UIComponent createConfigurator(UIAdapter<UIComponentGroup> adapter) {
        int rowSize = (int) Math.sqrt(inventory.getSlots());
        int colSize = rowSize;
        if (inventory.getSlots() == 8) {
            rowSize = 4;
            colSize = 2;
        }

        var group = UIContainers.group(Sizing.fixed(18 * rowSize + 16), Sizing.fixed(18 * colSize + 16));
        group.padding(Insets.of(8));
        var container = UIContainers.grid(Sizing.fixed(18 * rowSize + 8), Sizing.fixed(18 * colSize + 8), rowSize, colSize);
        container.padding(Insets.of(4));

        int index = 0;
        for (int y = 0; y < colSize; y++) {
            for (int x = 0; x < rowSize; x++) {
                container.child(UIComponents.slot(inventory, index++)
                                .backgroundTexture(GuiTextures.SLOT)
                                .ingredientIO(IO.IN)
                                .canInsert(true)
                                .canExtract(true)
                                .positioning(Positioning.absolute(x * 18, y * 18)),
                        x, y);
            }
        }
        container.surface(Surface.UI_BACKGROUND_INVERSE);
        group.child(container);

        return group;
    }

}
