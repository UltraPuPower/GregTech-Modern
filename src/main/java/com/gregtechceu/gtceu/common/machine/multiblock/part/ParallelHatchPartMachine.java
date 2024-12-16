package com.gregtechceu.gtceu.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.IntInputComponent;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.fancy.FancyMachineUIComponent;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ParallelHatchPartMachine extends TieredPartMachine implements IFancyUIMachine, IParallelHatch {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ParallelHatchPartMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);
    private static final int MIN_PARALLEL = 1;

    private final int maxParallel;

    @Persisted
    @Getter
    private int currentParallel;

    public ParallelHatchPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        this.maxParallel = (int) Math.pow(4, tier - GTValues.EV);
    }

    public void setCurrentParallel(int parallelAmount) {
        this.currentParallel = Mth.clamp(parallelAmount, MIN_PARALLEL, this.maxParallel);
        for (IMultiController controller : this.getControllers()) {
            if (controller instanceof IRecipeLogicMachine rlm) {
                rlm.getRecipeLogic().markLastRecipeDirty();
            }
        }
    }

    @Override
    public void loadServerUI(@NotNull Player player, @NotNull UIContainerMenu<MetaMachine> menu,
                             @NotNull MetaMachine holder) {
        super.loadServerUI(player, menu, holder);
        menu.addServerboundMessage(ChangeParallelMessage.class, msg -> setCurrentParallel(msg.parallel()));
    }

    @Override
    public ParentUIComponent createBaseUIComponent(FancyMachineUIComponent component) {
        UIComponentGroup parallelAmountGroup = UIContainers.group(Sizing.fixed(100), Sizing.fixed(20));
        parallelAmountGroup.child(new IntInputComponent(this::getCurrentParallel, value -> {
            setCurrentParallel(value);
            component.containerAccess().screen().getMenu().sendMessage(new ChangeParallelMessage(value));
        }).setMin(MIN_PARALLEL).setMax(maxParallel));

        return parallelAmountGroup;
    }

    @Override
    @NotNull
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean canShared() {
        return false;
    }

    public record ChangeParallelMessage(int parallel) {}
}
