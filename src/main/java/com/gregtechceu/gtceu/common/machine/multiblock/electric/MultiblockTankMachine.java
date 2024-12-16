package com.gregtechceu.gtceu.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.fluids.PropertyFluidFilter;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.fancy.FancyMachineUIComponent;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MultiblockTankMachine extends MultiblockControllerMachine implements IFancyUIMachine {

    @Persisted
    @Getter
    @NotNull
    private final NotifiableFluidTank tank;

    public MultiblockTankMachine(IMachineBlockEntity holder, int capacity, @Nullable PropertyFluidFilter filter,
                                 Object... args) {
        super(holder);

        this.tank = createTank(capacity, filter, args);
    }

    protected NotifiableFluidTank createTank(int capacity, @Nullable PropertyFluidFilter filter, Object... args) {
        var fluidTank = new NotifiableFluidTank(this, 1, capacity, IO.BOTH);

        if (filter != null)
            fluidTank.setFilter(filter);

        return fluidTank;
    }

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                   BlockHitResult hit) {
        var superResult = super.onUse(state, world, pos, player, hand, hit);

        if (superResult != InteractionResult.PASS) return superResult;
        if (!isFormed()) return InteractionResult.FAIL;

        return InteractionResult.PASS; // Otherwise let MetaMachineBlock.use() open the UI
    }

    /////////////////////////////////////
    // *********** GUI ***********//
    /////////////////////////////////////

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {}

    @Override
    public ParentUIComponent createBaseUIComponent(FancyMachineUIComponent component) {
        var group = UIContainers.horizontalFlow(Sizing.fixed(90), Sizing.fixed(63));
        group.surface(Surface.UI_BACKGROUND_INVERSE)
                .padding(Insets.of(4));

        group.child(UIContainers.verticalFlow(Sizing.fill(), Sizing.fill())
                .child(UIComponents.label(Component.translatable("gtceu.gui.fluid_amount")))
                .child(UIComponents.label(this::getFluidLabel)
                        .color(Color.BLACK)
                        .shadow(true))
                .child(UIComponents.tank(tank.getStorages()[0])
                        .canInsert(true)
                        .canExtract(true)
                        .positioning(Positioning.absolute(60, 15)))
                .surface(Surface.UI_DISPLAY));
        return group;
    }

    private Component getFluidLabel() {
        return Component.literal(String.valueOf(tank.getFluidInTank(0).getAmount()));
    }

    //////////////////////////////////////
    // ***** LDLib SyncData ******//
    //////////////////////////////////////

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(MultiblockTankMachine.class,
            MultiblockControllerMachine.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
