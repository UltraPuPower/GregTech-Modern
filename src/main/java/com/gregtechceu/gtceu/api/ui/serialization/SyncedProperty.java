package com.gregtechceu.gtceu.api.ui.serialization;

import com.gregtechceu.gtceu.api.ui.util.Observable;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;

import org.jetbrains.annotations.ApiStatus;

@Accessors(fluent = true)
public class SyncedProperty<T> extends Observable<T> {

    @Getter
    private final int index;
    private final PacketBufSerializer<T> serializer;
    @ApiStatus.Internal
    @Getter
    private boolean needsSync;

    @ApiStatus.Internal
    public SyncedProperty(int index, Class<T> klass, T initial) {
        super(initial);

        this.index = index;
        this.serializer = PacketBufSerializer.get(klass);
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
