package com.gregtechceu.gtceu.api.transfer.fluid;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class CustomFluidTank extends FluidTank
                             implements IFluidHandlerModifiable, ITagSerializable<CompoundTag>, IContentChangeAware {

    @Getter
    @Setter
    protected Runnable onContentsChanged = () -> {};
    protected List<Runnable> onContentsChangedList = new ArrayList<>();

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
        onContentsChanged.run();
        onContentsChangedList.forEach(Runnable::run);
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

    public int addOnContentsChanged(Runnable onContentsChanged) {
        int size = this.onContentsChangedList.size();
        this.onContentsChangedList.add(onContentsChanged);
        return size;
    }

    public void removeOnContersChanged(int index) {
        if (this.onContentsChangedList.size() > index) {
            this.onContentsChangedList.remove(index);
        }
    }

    public void removeOnContersChanged(Runnable onContentsChanged) {
        this.onContentsChangedList.remove(onContentsChanged);
    }
}
