package com.gregtechceu.gtceu.api.ui.util.pond;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface UIAbstractContainerMenuExtension {

    void gtceu$attachToPlayer(Player player);

    void gtceu$readPropertySync(FriendlyByteBuf packet);

    void gtceu$handlePacket(FriendlyByteBuf packet, boolean clientbound);
}
