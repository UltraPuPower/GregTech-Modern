package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.util.pond.UISlotExtension;

import net.minecraft.world.inventory.Slot;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin implements UISlotExtension {

    @Unique
    private boolean gtceu$disabledOverride = false;

    @Unique
    private @Nullable PositionedRectangle gtceu$scissorArea = null;

    @Override
    public void gtceu$setDisabledOverride(boolean disabled) {
        this.gtceu$disabledOverride = disabled;
    }

    @Override
    public boolean gtceu$getDisabledOverride() {
        return this.gtceu$disabledOverride;
    }

    @Override
    public void gtceu$setScissorArea(@Nullable PositionedRectangle scissor) {
        this.gtceu$scissorArea = scissor;
    }

    @Override
    public @Nullable PositionedRectangle gtceu$getScissorArea() {
        return this.gtceu$scissorArea;
    }

    @Inject(method = "isActive", at = @At("TAIL"), cancellable = true)
    private void injectOverride(CallbackInfoReturnable<Boolean> cir) {
        if (!this.gtceu$disabledOverride) return;
        cir.setReturnValue(false);
    }
}
