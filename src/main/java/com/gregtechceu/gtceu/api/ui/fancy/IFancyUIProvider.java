package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface IFancyUIProvider {

    /**
     * Create the main page ui.
     */
    ParentUIComponent createMainPage(FancyMachineUIComponent widget);

    /**
     * Get the tab icon of this page.
     */
    UITexture getTabIcon();

    /**
     * Get the title of this page.
     */
    Component getTitle();

    /**
     * Attach configurators to the left subtab list.
     */
    default void attachSideTabs(TabsComponent configuratorPanel) {}

    /**
     * Attach configurators to the left panel.
     */
    default void attachConfigurators(ConfiguratorPanelComponent configuratorPanel) {}

    /**
     * Attach tooltips to the right panel
     */
    default void attachTooltips(TooltipsPanelComponent tooltipsPanel) {}

    default boolean hasPlayerInventory() {
        return true;
    }

    /**
     * Get sub tabs, for example, multiblock will show all its parts tabs.
     */
    default List<IFancyUIProvider> getSubTabs() {
        return Collections.emptyList();
    }

    /**
     * Get tab's Tooltips
     */
    default List<Component> getTabTooltips() {
        return Collections.emptyList();
    }

    /**
     * Get tab's Tooltips Component
     */
    @Nullable
    default TooltipComponent getTabTooltipComponent() {
        return null;
    }

    @Nullable
    default PageGroupingData getPageGroupingData() {
        return null;
    }

    record PageGroupingData(@Nullable String groupKey, int groupPositionWeight) {}
}
