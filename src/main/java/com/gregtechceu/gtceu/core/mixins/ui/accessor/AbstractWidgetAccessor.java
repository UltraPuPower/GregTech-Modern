package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {

    @Accessor("height")
    void gtceu$setHeight(int height);

    @Accessor("width")
    void gtceu$setWidth(int width);

    @Accessor("x")
    void gtceu$setX(int x);

    @Accessor("y")
    void gtceu$setY(int y);

    @Accessor("tooltip")
    Tooltip gtceu$getTooltip();
}
