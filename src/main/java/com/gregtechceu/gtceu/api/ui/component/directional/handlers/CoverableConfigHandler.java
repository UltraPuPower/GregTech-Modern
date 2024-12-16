package com.gregtechceu.gtceu.api.ui.component.directional.handlers;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.IUICover;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.*;
import com.gregtechceu.gtceu.api.ui.component.CoverConfigurator;
import com.gregtechceu.gtceu.api.ui.component.directional.IDirectionalConfigHandler;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.fancy.ConfiguratorPanelComponent;
import com.gregtechceu.gtceu.api.ui.fancy.FancyMachineUIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.api.ui.util.ClickData;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CoverableConfigHandler implements IDirectionalConfigHandler {

    private static final UITexture CONFIG_BUTTON_TEXTURE = UITextures.group(GuiTextures.IO_CONFIG_COVER_SETTINGS);

    private final ICoverable machine;
    private CustomItemStackHandler handler;
    private Direction side;

    private ConfiguratorPanelComponent panel;
    private ConfiguratorPanelComponent.FloatingTab coverConfigurator;

    private SlotComponent slotComponent;
    private CoverBehavior coverBehavior;

    public CoverableConfigHandler(ICoverable machine) {
        this.machine = machine;
        this.handler = createItemStackHandler();
    }

    private CustomItemStackHandler createItemStackHandler() {
        var handler = new CustomItemStackHandler(1) {

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };

        handler.setFilter(itemStack -> {
            if (itemStack.isEmpty()) return true;
            if (this.side == null) return false;
            return CoverPlaceBehavior.isCoverBehaviorItem(itemStack, () -> false,
                    coverDef -> ICoverable.canPlaceCover(coverDef, this.machine));
        });

        return handler;
    }

    @Override
    public UIComponent getSideSelectorWidget(SceneComponent scene, FancyMachineUIComponent machineUI) {
        FlowLayout group = UIContainers.horizontalFlow(Sizing.content(1), Sizing.content());
        this.panel = machineUI.configuratorPanel();

        group.child(slotComponent = (SlotComponent) UIComponents.slot(handler, 0)
                .changeListener(this::coverItemChanged)
                .positioning(Positioning.absolute(19, 0))
                .sizing(Sizing.fixed(18)));
        group.child(UIComponents.texture(UITextures.group(GuiTextures.SLOT, GuiTextures.IO_CONFIG_COVER_SLOT_OVERLAY))
                .positioning(Positioning.absolute(19, 0))
                .sizing(Sizing.fixed(18)));
        group.child(new PredicatedButtonComponent(CONFIG_BUTTON_TEXTURE, this::toggleConfigTab,
                () -> side != null && coverBehavior != null && machine.getCoverAtSide(side) instanceof IUICover)
                .positioning(Positioning.relative(0, 0))
                .sizing(Sizing.fixed(18)));

        checkCoverBehaviour();

        return group;
    }

    // FIXME make this be used somehow???
    // FIXME: This gets called twice in a single tick, causing two covers to exist simultaneously
    private void coverItemChanged() {
        closeConfigTab();

        if (!(panel.player() instanceof ServerPlayer serverPlayer) || side == null)
            return;

        var item = handler.getStackInSlot(0);
        if (machine.getCoverAtSide(side) != null) {
            machine.removeCover(false, side, serverPlayer);
        }

        if (!item.isEmpty() && machine.getCoverAtSide(side) == null) {
            if (item.getItem() instanceof IComponentItem componentItem) {
                for (IItemComponent component : componentItem.getComponents()) {
                    if (component instanceof CoverPlaceBehavior placeBehavior) {
                        machine.placeCoverOnSide(side, item, placeBehavior.coverDefinition(), serverPlayer);
                        break;
                    }
                }
            }
        }

        checkCoverBehaviour();
    }

    @Override
    public void onSideSelected(BlockPos pos, Direction side) {
        this.side = side;
        checkCoverBehaviour();
        closeConfigTab();
    }

    private void updateWidgetVisibility() {
        var sideSelected = this.side != null;
        slotComponent.enabled(sideSelected);
    }

    public void checkCoverBehaviour() {
        if (side == null)
            return;

        var coverBehaviour = machine.getCoverAtSide(side);
        if (coverBehaviour != this.coverBehavior) {
            this.coverBehavior = coverBehaviour;

            var attachItem = coverBehaviour == null ? ItemStack.EMPTY : coverBehaviour.getAttachItem();
            handler.setStackInSlot(0, attachItem);
            handler.onContentsChanged(0);
        }

        updateWidgetVisibility();
    }

    private void toggleConfigTab(ClickData cd) {
        if (this.coverConfigurator == null)
            openConfigTab();
        else
            closeConfigTab();
    }

    private void openConfigTab() {
        CoverConfigurator configurator = new CoverConfigurator(this.machine, this.side, this.coverBehavior) {

            @Override
            public Component getTitle() {
                // Uses the widget's own title
                return Component.empty();
            }

            @Override
            public UITexture getIcon() {
                return GuiTextures.CLOSE_ICON;
            }

            @Override
            public ParentUIComponent createConfigurator(UIAdapter<UIComponentGroup> adapter) {
                UIComponentGroup group = UIContainers.group(Sizing.content(), Sizing.content(10));

                if (side == null || !(coverable.getCoverAtSide(side) instanceof IUICover iuiCover))
                    return group;

                ParentUIComponent coverConfigurator = iuiCover.createUIWidget(adapter);
                coverConfigurator.moveTo(coverConfigurator.x() - 1, coverConfigurator.y() - 20);

                group.child(coverConfigurator);
                return group;
            }
        };

        this.coverConfigurator = this.panel.createFloatingTab(configurator);
        this.coverConfigurator.containerAccess(this.panel.containerAccess());
        this.panel.child(this.coverConfigurator);
        this.panel.expandTab(this.coverConfigurator);

        coverConfigurator.onClose(() -> {
            if (coverConfigurator != null) {
                this.panel.removeChild(this.coverConfigurator);
            }

            this.coverConfigurator = null;
        });
    }

    private void closeConfigTab() {
        if (this.coverConfigurator != null) {
            this.panel.collapseTab();
        }
    }

    @Override
    public ScreenSide getScreenSide() {
        return ScreenSide.RIGHT;
    }
}
