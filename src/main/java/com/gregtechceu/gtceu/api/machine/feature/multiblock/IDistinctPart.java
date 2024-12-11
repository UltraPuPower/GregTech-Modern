package com.gregtechceu.gtceu.api.machine.feature.multiblock;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.fancy.ConfiguratorPanelComponent;
import com.gregtechceu.gtceu.api.ui.fancy.IFancyConfiguratorButton;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/7/24
 * @implNote IDistinctPart
 */
public interface IDistinctPart extends IMultiPart {

    boolean isDistinct();

    void setDistinct(boolean isDistinct);

    @Override
    default void attachConfigurators(ConfiguratorPanelComponent configuratorPanel) {
        superAttachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new IFancyConfiguratorButton.Toggle(
                GuiTextures.BUTTON_DISTINCT_BUSES.getSubTexture(0, 0.5, 1, 0.5),
                GuiTextures.BUTTON_DISTINCT_BUSES.getSubTexture(0, 0, 1, 0.5),
                this::isDistinct, (button, pressed) -> setDistinct(pressed))
                .setTooltipsSupplier(pressed -> List.of(
                        Component.translatable("gtceu.multiblock.universal.distinct")
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW))
                                .append(Component.translatable(pressed ? "gtceu.multiblock.universal.distinct.yes" :
                                        "gtceu.multiblock.universal.distinct.no")))));
    }

    default void superAttachConfigurators(ConfiguratorPanelComponent configuratorPanel) {
        IMultiPart.super.attachConfigurators(configuratorPanel);
    }
}
