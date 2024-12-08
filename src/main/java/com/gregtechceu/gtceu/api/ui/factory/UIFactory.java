package com.gregtechceu.gtceu.api.ui.factory;

import com.gregtechceu.gtceu.api.ui.UIContainer;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
        var adapter = createAdapter(player, holder);
        if (adapter == null) return false;
        loadUITemplate(player, adapter.rootComponent, holder);

        NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, inv, player1) -> {
                    return new UIContainer(containerId, inv, adapter);
                }, getUITitle(holder, player)),
                buf -> {
                    buf.writeResourceLocation(this.uiFactoryId);
                    writeHolderToSyncData(buf, holder);
                });
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public final UIAdapter<RootContainer> initClientUI(FriendlyByteBuf serializedHolder) {
        T holder = readHolderFromSyncData(serializedHolder);
        if (holder == null) {
            return null;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        var adapter = createAdapter(player, holder);
        if (adapter == null) return null;
        loadUITemplate(player, adapter.rootComponent, holder);

        return adapter;
    }

    @Nullable
    public UIAdapter<RootContainer> createAdapter(Player player, T holder) {
        return UIAdapter.createWithoutScreen(0, 0, 176, 166, UIContainers::root);
    }

    public abstract void loadUITemplate(Player player, RootContainer rootComponent, T holder);

    public Component getUITitle(T holder, Player player) {
        return Component.empty();
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract T readHolderFromSyncData(FriendlyByteBuf syncData);

    protected abstract void writeHolderToSyncData(FriendlyByteBuf syncData, T holder);
}
