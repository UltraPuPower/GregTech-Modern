package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.*;
import com.gregtechceu.gtceu.api.ui.component.ButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.PlayerInventoryComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@ApiStatus.Internal
@Accessors(fluent = true, chain = true)
@Getter
public class FancyMachineUIComponent extends FlowLayout {

    protected final TitleBarComponent titleBar;
    protected final VerticalTabsComponent sideTabsComponent;
    protected final FlowLayout pageContainer;
    protected final PageSwitcher pageSwitcher;
    protected final ConfiguratorPanel configuratorPanel;
    protected final TooltipsPanel tooltipsPanel;

    @Nullable
    protected final PlayerInventoryComponent playerInventory;

    protected final IFancyUIProvider mainPage;

    /*
     * Current Page: The page visible in the UI
     * Current Home Page: The currently selected multiblock part's home page.
     */
    protected IFancyUIProvider currentPage;
    protected IFancyUIProvider currentHomePage;

    protected List<IFancyUIProvider> allPages;

    protected Deque<NavigationEntry> previousPages = new ArrayDeque<>();

    protected record NavigationEntry(IFancyUIProvider page, IFancyUIProvider homePage, Runnable onNavigation) {}

    public FancyMachineUIComponent(IFancyUIProvider mainPage,
                                      Sizing horizontalSizing, Sizing verticalSizing) {
        super(horizontalSizing, verticalSizing, Algorithm.HORIZONTAL);
        this.mainPage = mainPage;

        child(this.pageContainer = UIContainers.horizontalFlow(horizontalSizing, verticalSizing));

        if (mainPage.hasPlayerInventory()) {
            child(this.playerInventory = UIComponents.playerInventory());
            this.playerInventory.positioning(Positioning.absolute(2, height - 86));
        } else {
            playerInventory = null;
        }

        child(this.titleBar = new TitleBarComponent(width, this::navigateBack, this::openPageSwitcher));
        child(this.sideTabsComponent = new VerticalTabsComponent(this::navigate, -20, 0, 24, height));
        child(this.tooltipsPanel = new TooltipsPanel());
        child(this.configuratorPanel = new ConfiguratorPanel(-(24 + 2), height));
        this.pageSwitcher = new PageSwitcher(this::switchPage);

        surface(GuiTextures.BACKGROUND.copy()
                .setColor(Long.decode(ConfigHolder.INSTANCE.client.defaultUIColor).intValue() | 0xFF000000));
    }

    ////////////////////////////////////////
    // ********* NAVIGATION *********//
    ////////////////////////////////////////

    protected void navigate(IFancyUIProvider newPage) {
        navigate(newPage, this.currentHomePage);
    }

    protected void navigate(IFancyUIProvider nextPage, IFancyUIProvider nextHomePage) {
        if (nextPage != mainPage) {
            if (!this.previousPages.isEmpty() && this.previousPages.peek().page == nextPage) {
                // In case the user manually navigates back one step, just remove it from the navigation stack
                this.previousPages.pop();
            } else if (this.currentPage != null) {
                this.previousPages.push(new FancyMachineUIWidget.NavigationEntry(this.currentPage, this.currentHomePage, () -> {}));
            }
        } else {
            this.previousPages.clear();
        }

        performNavigation(nextPage, nextHomePage);
    }

    protected void navigateBack(ButtonComponent clickData) {
        FancyMachineUIWidget.NavigationEntry navigationEntry = previousPages.pop();

        performNavigation(navigationEntry.page, navigationEntry.homePage);
        navigationEntry.onNavigation.run();
    }

    protected void performNavigation(IFancyUIProvider nextPage, IFancyUIProvider nextHomePage) {
        if (currentHomePage != nextHomePage)
            setupSideTabs(nextHomePage);

        this.currentPage = nextPage;
        this.currentHomePage = nextHomePage;

        if (currentPage != currentHomePage) {
            // Ensure the home page's basic layout is applied before navigating to another page:
            setupFancyUI(currentHomePage);
        }

        setupFancyUI(nextPage, nextPage.hasPlayerInventory());
    }

    ///////////////////////////////////////////////
    // *********** PAGE SWITCHER ***********//
    ///////////////////////////////////////////////

    protected void openPageSwitcher(ButtonComponent clickData) {
        pageSwitcher.setPageList(allPages, currentHomePage);

        // If we're in another tab of the current page, ensure nav to its main tab when closing the page switcher:
        if (currentPage != currentHomePage && !previousPages.isEmpty()) {
            previousPages.pop();
        }

        this.sideTabsComponent.setVisible(false);
        this.sideTabsComponent.setActive(false);

        this.previousPages.push(new FancyMachineUIWidget.NavigationEntry(currentHomePage, currentHomePage, () -> {
            sideTabsComponent.setVisible(true);
            sideTabsComponent.setActive(true);
        }));

        this.currentPage = this.pageSwitcher;
        this.currentHomePage = this.pageSwitcher;

        setupFancyUI(this.pageSwitcher);
    }

    protected void switchPage(IFancyUIProvider nextHomePage) {
        // Ensure that the back button always leads back to the main page:
        this.currentHomePage = mainPage;
        this.currentPage = mainPage;
        this.previousPages.clear();

        sideTabsComponent.setVisible(true);
        sideTabsComponent.setActive(true);

        setupSideTabs(this.currentHomePage);
        navigate(nextHomePage, nextHomePage);
    }

    //////////////////////////////////////////////
    // *********** UI RENDERING ***********//
    //////////////////////////////////////////////

    protected void setupFancyUI(IFancyUIProvider fancyUI) {
        this.setupFancyUI(fancyUI, fancyUI.hasPlayerInventory());
    }

    protected void setupFancyUI(IFancyUIProvider fancyUI, boolean showInventory) {
        clearUI();

        sideTabsComponent.selectTab(fancyUI);
        titleBar.updateState(
                currentHomePage,
                !this.previousPages.isEmpty(),
                this.allPages.size() > 1 && this.currentPage != this.pageSwitcher);

        var page = fancyUI.createMainPage(this);

        // layout
        var size = new Size(Math.max(172, page.getSize().width + border * 2),
                Math.max(86, page.getSize().height + border * 2));
        setSize(new Size(size.width,
                size.height + (!showInventory || playerInventory == null ? 0 : playerInventory.getSize().height)));
        if (LDLib.isRemote() && getGui() != null) {
            getGui().setSize(getSize().width, getSize().height);
        }
        this.sideTabsComponent.setSize(new Size(24, size.height));
        this.pageContainer.sizing(size);
        this.tooltipsPanel.setSelfPosition(new Position(size.width + 2, 2));

        setupInventoryPosition(showInventory, size);

        // setup
        this.pageContainer.addWidget(page);
        page.setSelfPosition(new Position(
                (pageContainer.getSize().width - page.getSize().width) / 2,
                (pageContainer.getSize().height - page.getSize().height) / 2));
        fancyUI.attachConfigurators(configuratorPanel);
        configuratorPanel
                .setSelfPosition(new Position(-24 - 2, getGui().getHeight() - configuratorPanel.getSize().height - 4));
        fancyUI.attachTooltips(tooltipsPanel);

        titleBar.sizing(Sizing.fill(), Sizing.fixed(titleBar.height()));
        titleBar.layout();
    }

    private void setupInventoryPosition(boolean showInventory, Size parentSize) {
        if (this.playerInventory == null)
            return;

        this.playerInventory.moveTo((parentSize.width - playerInventory.width()) / 2, parentSize.height);

        this.playerInventory.setActive(showInventory);
        this.playerInventory.setVisible(showInventory);
    }

    protected void clearUI() {
        this.pageContainer.clearChildren();
        this.configuratorPanel.clear();
        this.tooltipsPanel.clear();
    }

    protected void setupSideTabs(IFancyUIProvider currentHomePage) {
        this.sideTabsComponent.clearSubTabs();
        currentHomePage.attachSideTabs(sideTabsComponent);
    }
}
