package com.gregtechceu.gtceu.api.ui.factory;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine2;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MachineUIFactory extends UIFactory<MetaMachine> {

    public static final MachineUIFactory INSTANCE = new MachineUIFactory();

    public MachineUIFactory() {
        super(GTCEu.id("machine"));
    }

    @Override
    public void loadUITemplate(Player player, RootContainer rootComponent, MetaMachine holder) {
        if (holder instanceof IUIMachine2 machine) {
            machine.loadUITemplate(player, rootComponent);
        }
    }

    @Override
    protected MetaMachine readHolderFromSyncData(FriendlyByteBuf syncData) {
        Level world = Minecraft.getInstance().level;
        if (world == null) return null;
        if (world.getBlockEntity(syncData.readBlockPos()) instanceof IMachineBlockEntity holder) {
            return holder.getMetaMachine();
        }
        return null;
    }

    @Override
    protected void writeHolderToSyncData(FriendlyByteBuf syncData, MetaMachine holder) {
        syncData.writeBlockPos(holder.getPos());
    }
}
