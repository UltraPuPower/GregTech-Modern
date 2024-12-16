package com.gregtechceu.gtceu.api.machine.fancyconfigurator;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;

import net.minecraft.network.chat.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Accessors(chain = true)
public class FancyTankConfigurator implements IFancyConfigurator {

    private final CustomFluidTank[] tanks;

    @Getter
    private final Component title;

    @Getter
    @Setter
    private List<Component> tooltips = Collections.emptyList();

    public FancyTankConfigurator(CustomFluidTank[] tanks, Component title) {
        this.tanks = tanks;
        this.title = title;
    }

    @Override
    public UITexture getIcon() {
        return GuiTextures.BUTTON_FLUID_OUTPUT;
    }

    @Override
    public UIComponent createConfigurator(UIAdapter<UIComponentGroup> adapter) {
        int rowSize = (int) Math.sqrt(tanks.length);
        int colSize = rowSize;
        if (tanks.length == 8) {
            rowSize = 4;
            colSize = 2;
        }

        var group = UIContainers.group(Sizing.content(8), Sizing.content(8));
        var container = UIContainers.grid(Sizing.content(4), Sizing.content(4), rowSize, colSize);

        int index = 0;
        for (int y = 0; y < colSize; y++) {
            for (int x = 0; x < rowSize; x++) {
                container.child(UIComponents.tank(tanks[index++], 0)
                        .backgroundTexture(GuiTextures.FLUID_SLOT)
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
