package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultilineTextField.class)
public interface MultilineTextFieldAccessor {

    @Accessor("width")
    void ui$setWidth(int width);

    @Accessor("selectCursor")
    void ui$setSelectCursor(int width);

    @Accessor("selectCursor")
    int ui$getSelectCursor();
}
