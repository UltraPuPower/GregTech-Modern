package com.gregtechceu.gtceu.integration.ae2.gui.widget;

import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.TextBoxComponent;

import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import net.minecraft.network.chat.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
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

    private TextBoxComponent textField;

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
        this.textField = UIComponents.textBox(Sizing.fill(), this.getText())
                .textSupplier(this::getText);
        this.textField.positioning(Positioning.absolute(0, 0));
        this.textField.onChanged().subscribe(this::setText);
        this.child(UIComponents.toggleButton(this::isInputting,
                                        pressed -> {
                                            isInputting = pressed;
                                            if (pressed && !this.children.contains(textField)) {
                                                this.child(textField);
                                            } else {
                                                onConfirm.accept(text);
                                                this.removeChild(textField);
                                            }
                                        })
                                .texture(UITextures.group(GuiTextures.VANILLA_BUTTON, UITextures.text(Component.literal("✎"))),
                        UITextures.group(GuiTextures.VANILLA_BUTTON, UITextures.text(Component.literal("✔"))))
                                .sizing(Sizing.fill(), Sizing.fill()))
                .tooltip(Arrays.asList(hoverTexts));
        this.child(textField);
    }

}
