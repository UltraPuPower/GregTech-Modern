package com.gregtechceu.gtceu.api.ui.factory;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.IUICover;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CoverUIFactory extends UIFactory<CoverBehavior> {

    public static final CoverUIFactory INSTANCE = new CoverUIFactory();

    public CoverUIFactory() {
        super(GTCEu.id("cover"));
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<CoverBehavior> menu, CoverBehavior holder) {
        if (holder instanceof IUICover cover) {
            cover.loadServerUI(player, menu, holder);
        }
    }

    @Override
    public void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, CoverBehavior holder) {
        if (holder instanceof IUICover cover) {
            cover.loadClientUI(player, adapter, holder);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected CoverBehavior readHolderFromSyncData(FriendlyByteBuf syncData) {
        Level world = Minecraft.getInstance().level;
        if (world == null) return null;
        var pos = syncData.readBlockPos();
        var side = syncData.readEnum(Direction.class);
        var coverable = GTCapabilityHelper.getCoverable(world, pos, side);
        if (coverable != null) {
            return coverable.getCoverAtSide(side);
        }
        return null;
    }

    @Override
    protected void writeHolderToSyncData(FriendlyByteBuf syncData, CoverBehavior holder) {
        syncData.writeBlockPos(holder.coverHolder.getPos());
        syncData.writeEnum(holder.attachedSide);
    }
}
