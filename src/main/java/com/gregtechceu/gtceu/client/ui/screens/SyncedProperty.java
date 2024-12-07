package com.gregtechceu.gtceu.client.ui.screens;

import com.gregtechceu.gtceu.api.ui.serialization.PacketBufSerializer;
import com.gregtechceu.gtceu.api.ui.util.Observable;

import net.minecraft.network.FriendlyByteBuf;

import org.jetbrains.annotations.ApiStatus;

public class SyncedProperty<T> extends Observable<T> {

    private final int index;
    private final PacketBufSerializer<T> serializer;
    private boolean needsSync;

    @ApiStatus.Internal
    public SyncedProperty(int index, Class<T> klass, T initial) {
        super(initial);

        this.index = index;
        this.serializer = PacketBufSerializer.get(klass);
    }

    public int index() {
        return index;
    }

    @ApiStatus.Internal
    public boolean needsSync() {
        return needsSync;
    }

    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        needsSync = false;

        serializer.serializer().accept(buf, value);
    }

    @ApiStatus.Internal
    public void read(FriendlyByteBuf buf) {
        set(serializer.deserializer().apply(buf));
    }

    @Override
    protected void notifyObservers(T value) {
        super.notifyObservers(value);

        this.needsSync = true;
    }

    public void markDirty() {
        notifyObservers(value);
    }
}
