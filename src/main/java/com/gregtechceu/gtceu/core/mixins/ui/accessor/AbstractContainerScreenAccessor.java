package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    @Accessor("imageWidth")
    void gtceu$setImageWidth(int width);

    @Accessor("imageHeight")
    void gtceu$setImageHeight(int height);

    @Accessor("leftPos")
    void gtceu$setLeftPos(int width);

    @Accessor("topPos")
    void gtceu$setTopPos(int height);
}
