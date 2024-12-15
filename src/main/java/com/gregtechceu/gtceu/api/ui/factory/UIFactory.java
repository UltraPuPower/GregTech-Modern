package com.gregtechceu.gtceu.api.ui.factory;

import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.UIContainerScreen;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class UIFactory<T> {

    public static final Map<ResourceLocation, UIFactory<?>> FACTORIES = new HashMap<>();

    public final ResourceLocation uiFactoryId;

    public UIFactory(ResourceLocation uiFactoryId) {
        this.uiFactoryId = uiFactoryId;
    }

    public static void register(UIFactory<?> factory) {
        FACTORIES.put(factory.uiFactoryId, factory);
    }

    public final boolean openUI(final T holder, ServerPlayer player) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, inv, player1) -> {
                    return new UIContainerMenu<>(containerId, inv, this, holder, false);
                }, getUITitle(holder, player)),
                buf -> {
                    buf.writeResourceLocation(this.uiFactoryId);
                    writeHolderToSyncData(buf, holder);
                });
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public final T readClientHolder(FriendlyByteBuf serializedHolder) {
        return readHolderFromSyncData(serializedHolder);
    }

    @Nullable
    public UIAdapter<UIComponentGroup> createAdapter(Player player, T holder, UIContainerScreen screen) {
        return UIAdapter.create(screen, UIContainers::group);
    }

    public abstract void loadServerUI(Player player, UIContainerMenu<T> menu, T holder);

    @OnlyIn(Dist.CLIENT)
    public abstract void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, T holder);

    public Component getUITitle(T holder, Player player) {
        return Component.empty();
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract T readHolderFromSyncData(FriendlyByteBuf syncData);

    protected abstract void writeHolderToSyncData(FriendlyByteBuf syncData, T holder);

}
