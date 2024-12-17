package com.gregtechceu.gtceu.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidType;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PumpHatchPartMachine extends FluidHatchPartMachine {

    public PumpHatchPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, 0, IO.OUT, FluidType.BUCKET_VOLUME, 1, args);
    }

    @Override
    protected NotifiableFluidTank createTank(int initialCapacity, int slots, Object... args) {
        return super.createTank(initialCapacity, slots)
                .setFilter(fluidStack -> fluidStack.getFluid().is(GTMaterials.Water.getFluidTag()));
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        // TODO implement
    }

    @Override
    public void loadClientUI(Player player, UIAdapter<StackLayout> adapter, MetaMachine holder) {
        FlowLayout group = UIContainers.verticalFlow(Sizing.fixed(176), Sizing.fixed(166));
        adapter.rootComponent.child(group);

        group.child(UIComponents.label(getBlockState().getBlock().getName()))
                .child(UIContainers.verticalFlow(Sizing.fixed(81), Sizing.fixed(55))
                        .child(UIComponents.label(Component.translatable("gtceu.gui.fluid_amount")))
                        .child(UIComponents
                                .label(() -> Component.literal(String.valueOf(tank.getFluidInTank(0).getAmount())))
                                .color(Color.BLACK)
                                .shadow(true))
                        .child(UIComponents.tank(tank.getStorages()[0])
                                .canInsert(io.support(IO.IN))
                                .canExtract(true)
                                .positioning(Positioning.absolute(90, 35)))
                        .child(UIComponents
                                .toggleButton(GuiTextures.BUTTON_FLUID_OUTPUT, this::isWorkingEnabled,
                                        this::setWorkingEnabled)
                                .shouldUseBaseBackground()
                                .tooltip(List.of(Component.translatable("gtceu.gui.fluid_auto_input.tooltip")))
                                .positioning(Positioning.absolute(7, 53))
                                .sizing(Sizing.fixed(18)))
                        .surface(Surface.UI_DISPLAY)
                        .positioning(Positioning.absolute(7, 16)))
                .child(UIComponents.playerInventory(player.getInventory(), GuiTextures.SLOT)
                        .positioning(Positioning.absolute(7, 84)))
                .surface(Surface.UI_BACKGROUND);
    }
}
