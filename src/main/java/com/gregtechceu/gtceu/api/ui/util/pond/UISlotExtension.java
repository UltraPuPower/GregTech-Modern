package com.gregtechceu.gtceu.api.ui.util.pond;

import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;

import org.jetbrains.annotations.Nullable;

public interface UISlotExtension {

    default void gtceu$setDisabledOverride(boolean disabled) {
        throw new IllegalStateException("Implemented in AbstractContainerMenuMixin");
    }

    default boolean gtceu$getDisabledOverride() {
        throw new IllegalStateException("Implemented in AbstractContainerMenuMixin");
    }

    default void gtceu$setScissorArea(@Nullable PositionedRectangle scissor) {
        throw new IllegalStateException("Implemented in AbstractContainerMenuMixin");
    }

    @Nullable
    default PositionedRectangle gtceu$getScissorArea() {
        throw new IllegalStateException("Implemented in AbstractContainerMenuMixin");
    }
}
