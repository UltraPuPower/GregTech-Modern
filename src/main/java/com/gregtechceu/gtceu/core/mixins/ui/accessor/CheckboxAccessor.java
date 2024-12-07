package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.client.gui.components.Checkbox;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Checkbox.class)
public interface CheckboxAccessor {

    @Accessor("selected")
    void gtceu$setSelected(boolean selected);
}
