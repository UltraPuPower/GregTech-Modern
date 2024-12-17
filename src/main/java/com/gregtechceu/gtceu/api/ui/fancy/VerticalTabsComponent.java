package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.texture.ResourceTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@ApiStatus.Internal
public class VerticalTabsComponent extends TabsComponent {

    public VerticalTabsComponent(Consumer<IFancyUIProvider> onTabClick) {
        super(onTabClick);
        ResourceTexture tabsLeft = UITextures.resource(GTCEu.id("textures/gui/tab/tabs_left.png"), 0, 0, 64, 84);
        setTabTexture(tabsLeft.getSubTexture(0, 1 / 3f, 0.5f, 1 / 3f));
        setTabHoverTexture(tabsLeft.getSubTexture(0.5f, 1 / 3f, 0.5f, 1 / 3f));
        setTabPressedTexture(tabsLeft.getSubTexture(0.5f, 1 / 3f, 0.5f, 1 / 3f));
    }

    @Override
    public boolean hasButton() {
        return false;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        var hoveredTab = getHoveredTab(mouseX, mouseY);
        if (hoveredTab == null) {
            return;
        }
        updateTooltip(hoveredTab);
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        var hoveredTab = getHoveredTab(mouseX, mouseY);
        // main tab
        drawTab(mainTab, graphics, mouseX, mouseY, x, y + 8, 24, 24, hoveredTab);
        for (int i = 0; i < subTabs.size(); ++i) {
            drawTab(subTabs.get(i), graphics, mouseX, mouseY, x, y + 8 + 24 * (i + 1), 24, 24, hoveredTab);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public IFancyUIProvider getHoveredTab(double mouseX, double mouseY) {
        // main tab
        if (UIComponent.isMouseOver(x, y + 8, 24, 24, mouseX, mouseY)) {
            return mainTab;
        }
        // others
        int i = ((int) mouseY - y - 24 - 8) / 24;
        if (i >= 0 && i < subTabs.size()) {
            return subTabs.get(i);
        }
        return null;
    }
}
