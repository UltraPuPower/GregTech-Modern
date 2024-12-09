package com.gregtechceu.gtceu.api.ui.factory;

import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.container.RootContainer;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
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
        final RootContainer rootComponent = UIContainers.root(Sizing.fill(), Sizing.fill());
        loadUITemplate(player, rootComponent, holder);

        NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, inv, player1) -> {
            return new UIContainerMenu<>(containerId, inv, rootComponent, holder);
        }, getUITitle(holder, player)),
                buf -> {
                    buf.writeResourceLocation(this.uiFactoryId);
                    writeHolderToSyncData(buf, holder);
                });
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public final void initClientUI(FriendlyByteBuf serializedHolder, UIContainerMenu<T> container) {
        T holder = readHolderFromSyncData(serializedHolder);
        if (holder == null) {
            return;
        }
        container.setHolder(holder);
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
