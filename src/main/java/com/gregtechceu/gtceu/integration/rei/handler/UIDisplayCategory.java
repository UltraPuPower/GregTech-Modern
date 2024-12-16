package com.gregtechceu.gtceu.integration.rei.handler;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;

import java.util.List;

public abstract class UIDisplayCategory<T extends UIREIDisplay<?>> implements DisplayCategory<T> {

    @Override
    public List<Widget> setupDisplay(T display, Rectangle bounds) {
        return display.createWidget(bounds);
    }
}
