package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.serialization.NetworkException;
import com.gregtechceu.gtceu.api.ui.serialization.PacketBufSerializer;
import com.gregtechceu.gtceu.api.ui.util.pond.UIAbstractContainerMenuExtension;
import com.gregtechceu.gtceu.client.ui.ScreenInternals;
import com.gregtechceu.gtceu.client.ui.screens.ScreenhandlerMessageData;
import com.gregtechceu.gtceu.client.ui.screens.SyncedProperty;
import com.gregtechceu.gtceu.client.ui.screens.UIAbstractContainerMenu;
import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin implements UIAbstractContainerMenu, UIAbstractContainerMenuExtension {

    @Shadow
    private boolean suppressRemoteUpdates;

    @Unique
    private final List<SyncedProperty<?>> gtceu$properties = new ArrayList<>();

    @Unique
    private final Map<Class<?>, ScreenhandlerMessageData<?>> gtceu$messages = new LinkedHashMap<>();
    @Unique
    private final List<ScreenhandlerMessageData<?>> gtceu$clientboundMessages = new ArrayList<>();
    @Unique
    private final List<ScreenhandlerMessageData<?>> gtceu$serverboundMessages = new ArrayList<>();

    @Unique
    private Player gtceu$player = null;

    @Override
    public void gtceu$attachToPlayer(Player player) {
        this.gtceu$player = player;
    }

    @Override
    public Player player() {
        return this.gtceu$player;
    }

    @Override
    public <R extends Record> void addServerboundMessage(Class<R> messageClass, Consumer<R> handler) {
        int id = this.gtceu$serverboundMessages.size();

        var messageData = new ScreenhandlerMessageData<>(id, false, PacketBufSerializer.get(messageClass), handler);
        this.gtceu$serverboundMessages.add(messageData);

        if (this.gtceu$messages.put(messageClass, messageData) != null) {
            throw new NetworkException(messageClass + " is already registered as a message!");
        }
    }

    @Override
    public <R extends Record> void addClientboundMessage(Class<R> messageClass, Consumer<R> handler) {
        int id = this.gtceu$clientboundMessages.size();

        var messageData = new ScreenhandlerMessageData<>(id, true, PacketBufSerializer.get(messageClass), handler);
        this.gtceu$clientboundMessages.add(messageData);

        if (this.gtceu$messages.put(messageClass, messageData) != null) {
            throw new NetworkException(messageClass + " is already registered as a message!");
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <R extends Record> void sendMessage(@NotNull R message) {
        if (this.gtceu$player == null) {
            throw new NetworkException("Tried to send a message before player was attached");
        }

        ScreenhandlerMessageData messageData = this.gtceu$messages.get(message.getClass());

        if (messageData == null) {
            throw new NetworkException("Tried to send message of unknown type " + message.getClass());
        }

        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(messageData.id());
        messageData.serializer().serializer().accept(buf, message);

        if (messageData.clientbound()) {
            if (!(this.gtceu$player instanceof ServerPlayer serverPlayer)) {
                throw new NetworkException("Tried to send clientbound message on the server");
            }

            GTNetwork.NETWORK.sendToPlayer(new ScreenInternals.LocalPacket(buf), serverPlayer);
        } else {
            if (!this.gtceu$player.level().isClientSide) {
                throw new NetworkException("Tried to send serverbound message on the client");
            }

            this.gtceu$sendToServer(buf);
        }
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private void gtceu$sendToServer(FriendlyByteBuf data) {
        GTNetwork.NETWORK.sendToServer(new ScreenInternals.LocalPacket(data));
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void gtceu$handlePacket(FriendlyByteBuf buf, boolean clientbound) {
        int id = buf.readVarInt();
        ScreenhandlerMessageData messageData = (clientbound ? this.gtceu$clientboundMessages :
                this.gtceu$serverboundMessages).get(id);

        messageData.handler().accept(messageData.serializer().deserializer().apply(buf));
    }

    @Override
    public <T> SyncedProperty<T> createProperty(Class<T> clazz, T initial) {
        var prop = new SyncedProperty<>(this.gtceu$properties.size(), clazz, initial);
        this.gtceu$properties.add(prop);
        return prop;
    }

    @Override
    public void gtceu$readPropertySync(FriendlyByteBuf buf) {
        int count = buf.readVarInt();

        for (int i = 0; i < count; i++) {
            int idx = buf.readVarInt();
            this.gtceu$properties.get(idx).read(buf);
        }
    }

    @Inject(method = "sendAllDataToRemote", at = @At("RETURN"))
    private void syncOnSyncState(CallbackInfo ci) {
        this.syncProperties();
    }

    @Inject(method = "broadcastChanges", at = @At("RETURN"))
    private void syncOnSendContentUpdates(CallbackInfo ci) {
        if (suppressRemoteUpdates) return;

        this.syncProperties();
    }

    private void syncProperties() {
        if (this.gtceu$player == null) return;
        if (!(this.gtceu$player instanceof ServerPlayer player)) return;

        int count = 0;

        for (var property : this.gtceu$properties) {
            if (property.needsSync()) count++;
        }

        if (count == 0) return;

        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(count);

        for (var prop : gtceu$properties) {
            if (!prop.needsSync()) continue;

            buf.writeVarInt(prop.index());
            prop.write(buf);
        }

        GTNetwork.NETWORK.sendToPlayer(new ScreenInternals.LocalPacket(buf), player);
    }
}