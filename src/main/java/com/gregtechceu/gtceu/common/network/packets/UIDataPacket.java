package com.gregtechceu.gtceu.common.network.packets;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.util.pond.UIAbstractContainerMenuExtension;
import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

@AllArgsConstructor
public class UIDataPacket implements IPacket {

    public FriendlyByteBuf payload;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(payload.readableBytes());
        buf.writeBytes(payload);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        payload = new FriendlyByteBuf(copiedDataBuffer);
    }

    @Override
    public void execute(IHandlerContext handler) {
        if (handler.isClient()) {
            var containerMenu = Minecraft.getInstance().player.containerMenu;

            if (containerMenu == null) {
                GTCEu.LOGGER.error("Received local packet for null AbstractContainerMenu");
                return;
            }

            ((UIAbstractContainerMenuExtension) containerMenu).gtceu$handlePacket(payload, true);
        } else {
            var containerMenu = handler.getPlayer().containerMenu;

            if (containerMenu == null) {
                GTCEu.LOGGER.error("Received local packet for null AbstractContainerMenu");
                return;
            }

            ((UIAbstractContainerMenuExtension) containerMenu).gtceu$handlePacket(payload, false);
        }
    }
}
