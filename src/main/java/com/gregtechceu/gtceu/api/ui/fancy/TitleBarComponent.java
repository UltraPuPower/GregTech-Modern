package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.api.ui.component.LabelComponent;
import com.gregtechceu.gtceu.api.ui.component.TextureComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.util.ClickData;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TitleBarComponent extends UIComponentGroup {

    private static final int BORDER_SIZE = 3;
    private static final int HORIZONTAL_MARGIN = 8;
    private static final int HEIGHT = 16;
    private static final int BUTTON_WIDTH = 18;

    private static final float ROLL_SPEED = 0.7f;

    private int width;
    private boolean showBackButton = false;
    private boolean showMenuButton = false;
    private final int innerHeight;

    /**
     * The button group is rendered behind the main section and contains the back and menu buttons.
     * <p>
     * For easier texture reuse, the background is applied to the group itself, instead of the individual buttons.<br>
     * The button group therefore needs to be rendered behind the main section.
     */
    private final FlowLayout buttonGroup;
    private final UIComponent backButton;
    private final UIComponent menuButton;

    /**
     * The main section contains the current tab's icon and title text
     */
    private final FlowLayout mainSection;
    private final TextureComponent tabIcon;
    private final LabelComponent tabTitle;

    private boolean hasInit = false;

    protected TitleBarComponent(int parentWidth, Consumer<ClickData> onBackClicked, Consumer<ClickData> onMenuClicked) {
        super(Sizing.fixed(parentWidth), Sizing.fixed(HEIGHT));
        this.margins(Insets.of(0, 0, HORIZONTAL_MARGIN, HORIZONTAL_MARGIN));
        this.innerHeight = HEIGHT - BORDER_SIZE;
        this.width = parentWidth - (2 * HORIZONTAL_MARGIN);

        child(this.buttonGroup = UIContainers.horizontalFlow(Sizing.fill(), Sizing.fixed(innerHeight)));
        buttonGroup.positioning(Positioning.absolute(0, BORDER_SIZE));
        buttonGroup.surface(Surface.TITLE_BAR_BACKGROUND);
        buttonGroup.child(this.backButton = UIComponents.button(Component.literal(" <"), onBackClicked)
                .positioning(Positioning.absolute(0, BORDER_SIZE))
                .sizing(Sizing.fixed(BUTTON_WIDTH), Sizing.fixed(HEIGHT - BORDER_SIZE)));
        buttonGroup.child(this.menuButton = UIComponents.button(Component.literal("+"), onMenuClicked)
                        .positioning(Positioning.absolute(width - BUTTON_WIDTH, BORDER_SIZE))
                        .sizing(Sizing.fixed(BUTTON_WIDTH), Sizing.fixed(HEIGHT - BORDER_SIZE)));

        child(this.mainSection = UIContainers.horizontalFlow(Sizing.fill(), Sizing.fill()));
        mainSection.positioning(Positioning.absolute(BUTTON_WIDTH, 0));
        mainSection.surface(Surface.TITLE_BAR_BACKGROUND);
        mainSection.child(this.tabIcon = (TextureComponent) UIComponents.texture(UITexture.EMPTY)
                .sizing(Sizing.fixed(innerHeight - 2), Sizing.fixed(innerHeight - 2))
                .positioning(Positioning.absolute(BORDER_SIZE + 1, BORDER_SIZE + 1)));

        mainSection.child(this.tabTitle = (LabelComponent) UIComponents.label(Component.empty())
                .sizing(Sizing.content())
                .positioning(Positioning.absolute(BORDER_SIZE + innerHeight, BORDER_SIZE)));

        hasInit = true;
    }

    public void updateState(IFancyUIProvider currentPage, boolean showBackButton, boolean showMenuButton) {
        this.showBackButton = showBackButton;
        this.showMenuButton = showMenuButton;

        tabTitle.text(currentPage.getTitle().copy().withStyle(ChatFormatting.BLACK));
        tabTitle.maxWidth(this.width());
        tabTitle.rollSpeed(ROLL_SPEED);

        tabIcon.texture(currentPage.getTabIcon());

        if (showBackButton && !mainSection.children().contains(backButton)) {
            mainSection.child(backButton);
        } else if (!showBackButton) {
            mainSection.removeChild(backButton);
        }

        if (showMenuButton && !mainSection.children().contains(menuButton)) {
            mainSection.child(menuButton);
        } else if (!showBackButton) {
            mainSection.removeChild(menuButton);
        }

        updateLayout();
    }

    @Override
    protected void updateLayout() {
        super.updateLayout();
        if (!hasInit) return;

        var hiddenButtons = 2;
        if (showBackButton) hiddenButtons--;
        if (showMenuButton) hiddenButtons--;

        int buttonGroupWidth = this.width - (BUTTON_WIDTH * hiddenButtons);
        buttonGroup.sizing(Sizing.fixed(buttonGroupWidth), Sizing.fixed(innerHeight));
        buttonGroup.mount(this, showBackButton ? 0 : BUTTON_WIDTH, BORDER_SIZE);
        menuButton.mount(this, buttonGroupWidth - BUTTON_WIDTH, BORDER_SIZE);

        int mainSectionWidth = this.width - (BUTTON_WIDTH * 2);
        int titleWidth = mainSectionWidth - (2 * BORDER_SIZE) - innerHeight;
        mainSection.sizing(Sizing.fixed(mainSectionWidth), Sizing.fill());
        tabTitle.maxWidth(titleWidth);
        tabTitle.sizing(Sizing.fixed(titleWidth), Sizing.fill());
    }
}
