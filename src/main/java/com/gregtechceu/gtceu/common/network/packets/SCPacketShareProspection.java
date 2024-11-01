package com.gregtechceu.gtceu.common.network.packets;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.integration.map.ClientCacheManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import lombok.AllArgsConstructor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@AllArgsConstructor
public class SCPacketShareProspection implements CustomPacketPayload {

    public static final Type<SCPacketShareProspection> TYPE = new Type<>(GTCEu.id("share_prospection"));
    public static final StreamCodec<FriendlyByteBuf, SCPacketShareProspection> CODEC = StreamCodec.ofMember(SCPacketShareProspection::encode, SCPacketShareProspection::decode);

    private UUID sender;
    private UUID receiver;
    private String cacheName;
    private String key;
    private boolean isDimCache;
    private ResourceKey<Level> dimension;
    private CompoundTag data;
    private boolean first;

    public SCPacketShareProspection() {}

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(sender);
        buf.writeUUID(receiver);
        buf.writeUtf(cacheName);
        buf.writeUtf(key);
        buf.writeBoolean(isDimCache);
        buf.writeResourceKey(dimension);
        buf.writeNbt(data);
        buf.writeBoolean(first);
    }

    public static SCPacketShareProspection decode(FriendlyByteBuf buf) {
        var sender = buf.readUUID();
        var receiver = buf.readUUID();
        var cacheName = buf.readUtf();
        var key = buf.readUtf();
        var isDimCache = buf.readBoolean();
        var dimension = buf.readResourceKey(Registries.DIMENSION);
        var data = buf.readNbt();
        var first = buf.readBoolean();
        return new SCPacketShareProspection(sender, receiver, cacheName, key, isDimCache, dimension, data, first);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void execute(IPayloadContext handler) {
        if (handler.flow().isClientbound()) {
            if (first) {
                PlayerInfo senderInfo = Minecraft.getInstance().getConnection().getPlayerInfo(sender);
                if (senderInfo == null) {
                    return;
                }

                Component playerName = senderInfo.getTabListDisplayName() != null ? senderInfo.getTabListDisplayName() :
                        Component.literal(senderInfo.getProfile().getName());

                Minecraft.getInstance().player.sendSystemMessage(Component
                        .translatable("command.gtceu.share_prospection_data.notification", playerName));
            }
            ClientCacheManager.processProspectionShare(cacheName, key, isDimCache, dimension, data);
        } else {
            SCPacketShareProspection newPacket = new SCPacketShareProspection(sender, receiver,
                    cacheName, key,
                    isDimCache, dimension,
                    data, first);
            PacketDistributor.sendToPlayer(Platform.getMinecraftServer().getPlayerList().getPlayer(receiver), newPacket);
        }
    }
}
