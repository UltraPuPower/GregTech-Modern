package com.gregtechceu.gtceu.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/3/4
 * @implNote SteamHatchPartMachine
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SteamHatchPartMachine extends FluidHatchPartMachine {

    public static final int INITIAL_TANK_CAPACITY = 64 * FluidType.BUCKET_VOLUME;
    public static final boolean IS_STEEL = ConfigHolder.INSTANCE.machines.steelSteamMultiblocks;

    public SteamHatchPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, 0, IO.IN, SteamHatchPartMachine.INITIAL_TANK_CAPACITY, 1, args);
    }

    @Override
    protected NotifiableFluidTank createTank(int initialCapacity, int slots, Object... args) {
        return super.createTank(initialCapacity, slots)
                .setFilter(fluidStack -> fluidStack.getFluid().is(GTMaterials.Steam.getFluidTag()));
    }

    @Override
    public void loadClientUI(Player player, UIAdapter<StackLayout> adapter, MetaMachine holder) {
        FlowLayout group = UIContainers.horizontalFlow(Sizing.fixed(176), Sizing.fixed(166));
        group.child(UIComponents.label(getBlockState().getBlock().getName())
                .positioning(Positioning.absolute(6, 6)))
                .child(UIContainers.verticalFlow(Sizing.fixed(81), Sizing.fixed(55))
                        .child(UIComponents.label(Component.translatable("gtceu.gui.fluid_amount")))
                        .child(UIComponents
                                .label(() -> Component.literal(String.valueOf(tank.getFluidInTank(0).getAmount())))
                                .color(Color.BLACK)
                                .shadow(true))
                        .surface(GuiTextures.DISPLAY_STEAM.get(IS_STEEL)::draw)
                        .padding(Insets.both(3, 4))
                        .positioning(Positioning.absolute(7, 16)))
                .child(UIComponents.tank(tank.getStorages()[0])
                        .positioning(Positioning.absolute(90, 35)))
                .child(UIComponents.playerInventory(player.getInventory(), GuiTextures.SLOT_STEAM.get(IS_STEEL))
                        .positioning(Positioning.absolute(7, 84)))
                .surface(GuiTextures.BACKGROUND_STEAM.get(IS_STEEL)::draw);

        adapter.rootComponent.child(group);
    }
}
