package com.gregtechceu.gtceu.client.ui;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.util.pond.UIAbstractContainerMenuExtension;

import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ScreenInternals {

    @AllArgsConstructor
    public static class SyncPropertiesPacket implements IPacket {

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
                var screenHandler = Minecraft.getInstance().player.containerMenu;

                if (screenHandler == null) {
                    GTCEu.LOGGER.error("Received sync properties packet for null AbstractContainerMenu");
                    return;
                }

                ((UIAbstractContainerMenuExtension) screenHandler).gtceu$readPropertySync(payload);
            }
        }
    }

    public static class Client {

        public static void init() {
            MinecraftForge.EVENT_BUS.addListener(ScreenInternals.Client::afterScreenOpened);
        }

        public static void afterScreenOpened(ScreenEvent.Opening event) {
            if (event.getNewScreen() instanceof MenuAccess<?> handled) {
                ((UIAbstractContainerMenuExtension) handled.getMenu())
                        .gtceu$attachToPlayer(Minecraft.getInstance().player);
            }
        }
    }
}
