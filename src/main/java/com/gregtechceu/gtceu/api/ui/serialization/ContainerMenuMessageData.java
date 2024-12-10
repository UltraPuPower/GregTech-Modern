package com.gregtechceu.gtceu.api.ui.serialization;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public record ContainerMenuMessageData<T>(int id, boolean clientbound, PacketBufSerializer<T> serializer,
                                          Consumer<T> handler) {}
