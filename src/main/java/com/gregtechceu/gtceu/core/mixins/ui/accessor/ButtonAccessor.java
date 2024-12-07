package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.client.gui.components.Button;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Button.class)
public interface ButtonAccessor {

    @Mutable
    @Accessor("onPress")
    void gtceu$setOnPress(Button.OnPress onPress);
}
