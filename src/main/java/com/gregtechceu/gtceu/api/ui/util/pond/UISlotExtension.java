package com.gregtechceu.gtceu.api.ui.util.pond;

import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;

import org.jetbrains.annotations.Nullable;

public interface UISlotExtension {

    void gtceu$setDisabledOverride(boolean disabled);

    boolean gtceu$getDisabledOverride();

    void gtceu$setScissorArea(@Nullable PositionedRectangle scissor);

    @Nullable
    PositionedRectangle gtceu$getScissorArea();
}
