package com.gregtechceu.gtceu.api.ui.factory;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine2;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;

import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class MachineUIFactory extends UIFactory<MetaMachine> {

    public static final MachineUIFactory INSTANCE = new MachineUIFactory();

    public MachineUIFactory() {
        super(GTCEu.id("machine"));
    }

    @Override
    public @Nullable UIAdapter<RootContainer> createAdapter(Player player, MetaMachine holder) {
        UIModel model = UIModelLoader.get(holder.getDefinition().getId());
        if (model != null) {
            return model.createAdapterWithoutScreen(0, 0, 176, 166, RootContainer.class);
        }
        return super.createAdapter(player, holder);
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        if (menu.getHolder() instanceof IUIMachine2 machine) {
            machine.loadServerUI(player, menu, holder);
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void loadClientUI(Player player, UIAdapter<RootContainer> adapter, MetaMachine holder) {
        if (holder instanceof IUIMachine2 machine) {
            UIModel model = UIModelLoader.get(holder.getDefinition().getId());
            if (model != null) {
                return;
            }
            machine.loadClientUI(player, adapter);
        }
    }

    @OnlyIn(Dist.CLIENT)
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
