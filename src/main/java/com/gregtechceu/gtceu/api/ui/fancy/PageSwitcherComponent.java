package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.ButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.ScrollContainer;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PageSwitcherComponent implements IFancyUIProvider {

    private final Consumer<IFancyUIProvider> onPageSwitched;

    private List<IFancyUIProvider> pages = List.of();
    private IFancyUIProvider currentPage = null;

    public PageSwitcherComponent(Consumer<IFancyUIProvider> onPageSwitched) {
        this.onPageSwitched = onPageSwitched;
    }

    public void setPageList(List<IFancyUIProvider> allPages, IFancyUIProvider currentPage) {
        this.pages = allPages;
        this.currentPage = currentPage;
    }

    @Override
    public ParentUIComponent createMainPage(FancyMachineUIComponent widget) {
        var container = UIContainers.horizontalFlow(Sizing.fixed(176), Sizing.fixed(166));

        FlowLayout scrollableGroup;
        container.child(UIContainers.verticalScroll(Sizing.fixed(156), Sizing.fixed(146),
                        scrollableGroup = UIContainers.verticalFlow(Sizing.fill(), Sizing.fill()))
                .scrollbarThickness(8)
                .scrollbar(ScrollContainer.Scrollbar.custom(GuiTextures.SLIDER_BACKGROUND_VERTICAL.imageLocation,
                        GuiTextures.BUTTON.imageLocation)));

        var groupedPages = pages.stream().collect(Collectors.groupingBy(
                page -> Objects.requireNonNullElse(page.getPageGroupingData(), new PageGroupingData(null, -1))));

        final MutableInt currentY = new MutableInt(0);
        groupedPages.keySet().stream()
                .sorted(Comparator.comparingInt(PageGroupingData::groupPositionWeight))
                .forEachOrdered(group -> {
                    if (group.groupKey() != null) {
                        scrollableGroup.child(UIComponents.label(Component.translatable(group.groupKey()))
                                .positioning(Positioning.layout()));
                    }

                    final var currentPage = new MutableInt(0);
                    currentY.subtract(30); // To account for adding it back on the first page inside this group

                    groupedPages.get(group).forEach(page -> {
                        var index = currentPage.getAndIncrement();
                        var y = currentY.addAndGet(index % 5 == 0 ? 30 : 0); // Jump to the next row every 5 parts

                        var pageWidget = UIContainers.horizontalFlow(Sizing.fixed(25), Sizing.fixed(25));
                        pageWidget.positioning(Positioning.absolute((index % 5) * 30, y));
                        pageWidget.child(UIComponents.button(Component.empty(), clickData -> onPageSwitched.accept(page))
                                .renderer(ButtonComponent.Renderer.texture(GuiTextures.BACKGROUND.imageLocation,
                                        0, 0, 16, 16))
                                .sizing(Sizing.fill()));
                        pageWidget.child(UIComponents.texture(page.getTabIcon())
                                .positioning(Positioning.absolute(4, 4))
                                .sizing(Sizing.fixed(17)));
                        // For some reason, this doesn't work in any other way:
                        pageWidget.children().get(0).tooltip(page.getTitle());
                        scrollableGroup.child(pageWidget);
                    });

                    if (!groupedPages.get(group).isEmpty()) {
                        currentY.add(30);
                    }
                });

        return container;
    }

    @Override
    public UITexture getTabIcon() {
        return UITextures.text(Component.literal("+")).dropShadow(false).color(Color.BLACK.argb());
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.gui.title_bar.page_switcher");
    }

    @Override
    public boolean hasPlayerInventory() {
        return false;
    }

}
