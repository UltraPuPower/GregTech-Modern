package com.gregtechceu.gtceu.client.ui.screens;

import com.gregtechceu.gtceu.api.ui.serialization.PacketBufSerializer;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public record ContainerMenuMessageData<T>(int id, boolean clientbound, PacketBufSerializer<T> serializer,
                                          Consumer<T> handler) {}
