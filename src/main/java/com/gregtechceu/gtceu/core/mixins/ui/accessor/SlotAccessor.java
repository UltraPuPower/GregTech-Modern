package com.gregtechceu.gtceu.core.mixins.ui.accessor;

import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {

    @Accessor("index")
    void gtceu$setSlotIndex(int i);

    @Mutable
    @Accessor("x")
    void gtceu$setX(int x);

    @Mutable
    @Accessor("y")
    void gtceu$setY(int y);
}