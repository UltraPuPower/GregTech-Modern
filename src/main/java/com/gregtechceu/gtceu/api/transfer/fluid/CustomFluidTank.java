package com.gregtechceu.gtceu.api.transfer.fluid;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CustomFluidTank extends FluidTank
                             implements IFluidHandlerModifiable, ITagSerializable<CompoundTag>, IContentChangeAware {

    protected List<Runnable> onContentsChanged = new ArrayList<>();

    public CustomFluidTank(int capacity) {
        this(capacity, e -> true);
    }

    public CustomFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    public CustomFluidTank(FluidStack stack) {
        super(stack.getAmount());
        setFluid(stack);
    }

    @Override
    protected void onContentsChanged() {
        onContentsChanged.forEach(Runnable::run);
    }

    public CustomFluidTank copy() {
        FluidStack copiedStack = this.fluid.copy();
        CustomFluidTank copied = new CustomFluidTank(this.capacity, this.validator);
        copied.setFluid(copiedStack);
        return copied;
    }

    @Override
    public void setFluidInTank(int tank, FluidStack stack) {
        setFluid(stack);
    }

    @Override
    public void setFluid(FluidStack stack) {
        super.setFluid(stack);
        this.onContentsChanged();
    }

    @Override
    public CompoundTag serializeNBT() {
        return writeToNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        readFromNBT(nbt);
    }


    public Runnable getOnContentsChanged() {
        return () -> {
            for (Runnable r : this.onContentsChanged) {
                r.run();
            }
        };
    }

    public void setOnContentsChanged(Runnable onContentsChanged) {
        this.onContentsChanged.clear();
        this.onContentsChanged.add(onContentsChanged);
    }

    public int addOnContentsChanged(Runnable onContentsChanged) {
        int size = this.onContentsChanged.size();
        this.onContentsChanged.add(onContentsChanged);
        return size;
    }
    
    public void removeOnContersChanged(int index) {
        this.onContentsChanged.remove(index);
    }

    public void removeOnContersChanged(Runnable onContentsChanged) {
        this.onContentsChanged.remove(onContentsChanged);
    }
}
