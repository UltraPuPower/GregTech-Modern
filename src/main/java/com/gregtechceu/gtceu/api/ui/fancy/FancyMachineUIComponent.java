package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.api.ui.component.ButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.PlayerInventoryComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.ComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Size;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.Surface;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.AbstractContainerScreenAccessor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

// TODO remove fixed offsets in favor of dynamic ones
@ApiStatus.Internal
@Accessors(fluent = true, chain = true)
@Getter
public class FancyMachineUIComponent extends ComponentGroup {

    protected final TitleBarComponent titleBar;
    protected final VerticalTabsComponent sideTabsComponent;
    protected final FlowLayout pageContainer;
    protected final PageSwitcherComponent pageSwitcher;
    @Getter
    protected final ConfiguratorPanelComponent configuratorPanel;
    protected final TooltipsPanelComponent tooltipsPanel;

    @Nullable
    protected final PlayerInventoryComponent playerInventory;
    @Setter
    protected int border = 4;

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
        super(horizontalSizing, verticalSizing);
        this.mainPage = mainPage;

        child(this.pageContainer = UIContainers.horizontalFlow(horizontalSizing, verticalSizing));

        if (mainPage.hasPlayerInventory()) {
            child(this.playerInventory = UIComponents.playerInventory());
            this.playerInventory.positioning(Positioning.absolute(2, height - 86));
        } else {
            playerInventory = null;
        }

        child(this.titleBar = new TitleBarComponent(width, this::navigateBack, this::openPageSwitcher));
        child(this.sideTabsComponent = (VerticalTabsComponent) new VerticalTabsComponent(this::navigate)
                .positioning(Positioning.absolute(-20, 0))
                .sizing(Sizing.fixed(24), Sizing.fill()));
        child(this.tooltipsPanel = new TooltipsPanelComponent());
        child(this.configuratorPanel = new ConfiguratorPanelComponent());
        this.configuratorPanel.positioning(Positioning.absolute(-(24 + 2), height));
        this.pageSwitcher = new PageSwitcherComponent(this::switchPage);

        surface(Surface.UI_BACKGROUND);
        //surface(GuiTextures.BACKGROUND.copy()
        //        .setColor(Long.decode(ConfigHolder.INSTANCE.client.defaultUIColor).intValue() | 0xFF000000));
    }



    @Override
    public void init() {
        super.init();

        if (this.playerInventory != null) {
            this.playerInventory.setByInventory(player().getInventory());
        }

        this.allPages = Stream.concat(Stream.of(this.mainPage), this.mainPage.getSubTabs().stream()).toList();

        performNavigation(this.mainPage, this.mainPage);
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
                this.previousPages.push(new NavigationEntry(this.currentPage, this.currentHomePage, () -> {}));
            }
        } else {
            this.previousPages.clear();
        }

        performNavigation(nextPage, nextHomePage);
    }

    protected void navigateBack(ButtonComponent clickData) {
        NavigationEntry navigationEntry = previousPages.pop();

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

        this.removeChild(sideTabsComponent);

        this.previousPages.push(new NavigationEntry(currentHomePage, currentHomePage, () -> {
            this.child(sideTabsComponent);
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

        this.child(sideTabsComponent);

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
        int width = Math.max(172, page.width() + border() * 2);
        int height = Math.max(86, page.height() + border * 2);
        Size size = Size.of(width, height);
        this.sizing(Sizing.fixed(width),
                Sizing.fixed(height + (!showInventory || playerInventory == null ? 0 : playerInventory.height())));

        AbstractContainerScreen<?> screen = containerAccess().screen();
        if (screen != null) {
            ((AbstractContainerScreenAccessor) screen).gtceu$setImageWidth(width);
            ((AbstractContainerScreenAccessor) screen).gtceu$setImageHeight(height);

            int leftPos = (screen.width - width) / 2;
            int topPos = (screen.height - height) / 2;
            ((AbstractContainerScreenAccessor) screen).gtceu$setLeftPos(leftPos);
            ((AbstractContainerScreenAccessor) screen).gtceu$setTopPos(topPos);
            containerAccess().adapter().leftPos(leftPos);
            containerAccess().adapter().topPos(topPos);

            containerAccess().adapter().moveAndResize(0, 0, screen.width, screen.height);
        }

        this.sideTabsComponent.sizing(Sizing.fixed(24), Sizing.fixed(height));
        this.pageContainer.sizing(Sizing.fixed(width), Sizing.fixed(height));
        this.tooltipsPanel.positioning(Positioning.absolute(width + 2, 2));

        setupInventoryPosition(showInventory, size);

        // setup
        this.pageContainer.child(page);
        page.positioning(Positioning.absolute(
                (pageContainer.width() - page.width()) / 2,
                (pageContainer.height() - page.height()) / 2));
        fancyUI.attachConfigurators(configuratorPanel);
        configuratorPanel
                .positioning(Positioning.absolute(-24 - 2, screen.height - configuratorPanel.height() - 4));
        fancyUI.attachTooltips(tooltipsPanel);

        titleBar.sizing(Sizing.fill(), Sizing.fixed(titleBar.height()));
    }

    private void setupInventoryPosition(boolean showInventory, Size parentSize) {
        if (this.playerInventory == null)
            return;

        this.playerInventory.moveTo((parentSize.width() - playerInventory.width()) / 2, parentSize.height());

        if (showInventory && !this.children.contains(this.playerInventory)) {
            child(this.playerInventory);
        } else {
            removeChild(this.playerInventory);
        }
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
