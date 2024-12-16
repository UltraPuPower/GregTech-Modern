package com.gregtechceu.gtceu.common.machine.storage;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.TextBoxComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.Surface;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreativeComputationProviderMachine extends MetaMachine
                                                implements IUIMachine, IOpticalComputationProvider {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CreativeComputationProviderMachine.class, MetaMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    private int maxCWUt;
    private int lastRequestedCWUt;
    private int requestedCWUPerSec;
    @Persisted
    @Getter
    private boolean active;
    @Nullable
    private TickableSubscription computationSubs;

    public CreativeComputationProviderMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateComputationSubscription();
    }

    protected void updateComputationSubscription() {
        if (active) {
            this.computationSubs = subscribeServerTick(this::updateComputationTick);
        } else if (computationSubs != null) {
            computationSubs.unsubscribe();
            this.computationSubs = null;
            this.lastRequestedCWUt = 0;
            this.requestedCWUPerSec = 0;
        }
    }

    protected void updateComputationTick() {
        if (getOffsetTimer() % 20 == 0) {
            this.lastRequestedCWUt = requestedCWUPerSec / 20;
            this.requestedCWUPerSec = 0;
        }
    }

    @Override
    public int requestCWUt(
                           int cwut, boolean simulate, @NotNull Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        int requestedCWUt = active ? Math.min(cwut, maxCWUt) : 0;
        if (!simulate) {
            this.requestedCWUPerSec += requestedCWUt;
        }
        return requestedCWUt;
    }

    @Override
    public int getMaxCWUt(@NotNull Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return active ? maxCWUt : 0;
    }

    @Override
    public boolean canBridge(@NotNull Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return true;
    }

    public void setActive(boolean active) {
        this.active = active;
        updateComputationSubscription();
    }

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        var maxCWUt = menu.createProperty(int.class, "max_cwut", this.maxCWUt);
        var lastRequestedCWUt = menu.createProperty(int.class, "last_requested_cwut", this.lastRequestedCWUt);

        menu.addServerboundMessage(MaxCWUtMessage.class, msg -> this.maxCWUt = msg.maxCWUt());
        menu.addServerboundMessage(ActiveMessage.class, msg -> setActive(msg.active()));
    }

    @Override
    public void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, MetaMachine holder) {
        var menu = adapter.menu();
        UIComponentGroup rootComponent;
        adapter.rootComponent.child(rootComponent = UIContainers.group(Sizing.fixed(140), Sizing.fixed(95)));

        rootComponent.surface(Surface.UI_BACKGROUND);
        rootComponent.child(UIComponents.label(Component.literal("CWUt")));

        TextBoxComponent textBox = UIComponents.textBox(Sizing.fixed(122), String.valueOf(maxCWUt));
        textBox.onChanged().subscribe(value -> {
            this.maxCWUt = Integer.parseInt(value);
            textBox.sendMenuUpdate(new MaxCWUtMessage(maxCWUt));
        });
        rootComponent.child(textBox)
                .child(UIComponents.label(Component.translatable("gtceu.creative.computation.average"))
                        .positioning(Positioning.absolute(7, 42)))
                .child(UIComponents.label(() -> Component.literal(String.valueOf(lastRequestedCWUt)))
                        .positioning(Positioning.absolute(7, 54)));
        rootComponent.child(UIComponents.switchComponent((clickData, value) -> {
            setActive(value);
            menu.sendMessage(new ActiveMessage(value));
        })
                .supplier(this::isActive)
                .texture(UITextures.group(GuiTextures.VANILLA_BUTTON,
                        UITextures.text(Component.translatable("gtceu.creative.activity.off"))),
                        UITextures.group(GuiTextures.VANILLA_BUTTON,
                                UITextures.text(Component.translatable("gtceu.creative.activity.on"))))
                .positioning(Positioning.absolute(9, 66))
                .sizing(Sizing.fixed(122), Sizing.fixed(20)));
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public record MaxCWUtMessage(int maxCWUt) {}

    public record ActiveMessage(boolean active) {}
}
