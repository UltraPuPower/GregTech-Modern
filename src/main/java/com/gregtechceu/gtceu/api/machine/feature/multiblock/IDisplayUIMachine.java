package com.gregtechceu.gtceu.api.machine.feature.multiblock;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Insets;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.util.ClickData;
import com.gregtechceu.gtceu.api.ui.util.SlotGenerator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/3/16
 * @implNote IDisplayUIMachine
 */
public interface IDisplayUIMachine extends IUIMachine, IMultiController {

    default void addDisplayText(List<Component> textList) {
        for (var part : this.getParts()) {
            part.addMultiText(textList);
        }
    }

    default void handleDisplayClick(String componentData, ClickData clickData) {}

    default UITexture getScreenTexture() {
        return GuiTextures.DISPLAY;
    }

    @Override
    default void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        SlotGenerator.begin(menu::addSlot, 0, 0)
                .moveTo(7, 134)
                .playerInventory(menu.getPlayerInventory());
    }

    @Override
    default void loadClientUI(Player player, UIAdapter<StackLayout> adapter, MetaMachine holder) {
        StackLayout rootComponent = adapter.rootComponent;

        var screen = UIContainers.verticalFlow(Sizing.fixed(162), Sizing.fixed(121));
        screen.positioning(Positioning.absolute(7, 4));
        screen.padding(Insets.both(4, 5));

        screen.child(UIContainers.draggable(Sizing.fill(), Sizing.fill(),
                UIContainers.verticalFlow(Sizing.fill(), Sizing.fill())
                        .child(UIComponents.label(self().getBlockState().getBlock().getName()))
                        .child(UIComponents.componentPanel(this::addDisplayText)
                                .maxWidthLimit(150)
                                .clickHandler(this::handleDisplayClick))
                        .positioning(Positioning.absolute(4, 17))
                        .padding(Insets.of(4))))
                .surface((graphics, component) -> getScreenTexture().draw(graphics, 0, 0, component.x(), component.y(),
                        component.width(), component.height()));
        rootComponent.child(screen)
                .child(UIComponents.playerInventory(player.getInventory(), GuiTextures.SLOT)
                        .positioning(Positioning.absolute(3, 129)));
    }
}
