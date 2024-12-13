package com.gregtechceu.gtceu.integration.ae2.gui.widget;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.ToggleButtonComponent;

import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;

import net.minecraft.network.chat.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

@Accessors(chain = true)
public class AETextInputButtonComponent extends UIComponentGroup {

    @Setter
    private Consumer<String> onConfirm;

    @Getter
    @Setter
    private String text = "";

    private Component[] hoverTexts = new Component[0];

    @Getter
    private boolean isInputting;

    private UIComponent textField;

    public AETextInputButtonComponent(Sizing horizontalSizing, Sizing verticalSizing) {
        super(horizontalSizing, verticalSizing);
    }

    public AETextInputButtonComponent setButtonTooltips(Component... tooltipTexts) {
        this.hoverTexts = tooltipTexts;
        return this;
    }

    @Override
    public void init() {
        super.init();
        this.textField = new TextFieldWidget(
                0,
                0,
                getSizeWidth() - getSizeHeight() - 2,
                getSizeHeight(),
                this::getText,
                this::setText).setActive(false).setVisible(false);
        this.child(new ToggleButtonComponent(
                getSizeWidth() - getSizeHeight(),
                0,
                height(),
                height(),
                this::isInputting,
                pressed -> {
                    isInputting = pressed;
                    if (pressed && !this.children.contains(textField)) {
                        this.child(textField);
                    } else {
                        onConfirm.accept(text);
                        this.removeChild(textField);
                    }
                })
                .texture(
                        UITextures.group(GuiTextures.VANILLA_BUTTON, UITextures.text(Component.literal("✎")))),
                        UITextures.group(GuiTextures.VANILLA_BUTTON,  UITextures.text(Component.literal("✔"))))
                .setHoverTooltips(hoverTexts);
        this.child(textField);
    }
}
