package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    @Accessor("leftPos")
    int gtceu$getRootX();

    @Accessor("topPos")
    int gtceu$getRootY();
}
