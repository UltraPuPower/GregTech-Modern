package com.gregtechceu.gtceu.api.ui.util.pond;

import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;

import org.jetbrains.annotations.Nullable;

public interface UISlotExtension {

    default void gtceu$setDisabledOverride(boolean disabled) {
        throw new UnsupportedOperationException("Implemented in SlotMixin");
    }

    default boolean gtceu$getDisabledOverride() {
        throw new UnsupportedOperationException("Implemented in SlotMixin");
    }

    default void gtceu$setScissorArea(@Nullable PositionedRectangle scissor) {
        throw new UnsupportedOperationException("Implemented in SlotMixin");
    }

    @Nullable
    default PositionedRectangle gtceu$getScissorArea() {
        throw new UnsupportedOperationException("Implemented in SlotMixin");
    }
}
