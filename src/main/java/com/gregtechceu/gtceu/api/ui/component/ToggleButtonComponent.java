package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.texture.ResourceTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @author KilaBash
 * @date 2023/2/22
 * @implNote ToggleButtonWidget
 */
@Accessors(chain = true)
public class ToggleButtonComponent extends SwitchComponent {

    private final UITexture texture;
    @Setter
    private String tooltipText;

    public ToggleButtonComponent(BooleanSupplier isPressedCondition,
                                 BooleanConsumer setPressedExecutor) {
        this(GuiTextures.VANILLA_BUTTON, isPressedCondition, setPressedExecutor);
    }

    public ToggleButtonComponent(UITexture buttonTexture,
                                 BooleanSupplier isPressedCondition, BooleanConsumer setPressedExecutor) {
        super((clickData, aBoolean) -> setPressedExecutor.accept(aBoolean.booleanValue()));
        texture = buttonTexture;
        if (buttonTexture instanceof ResourceTexture resourceTexture) {
            setTexture(resourceTexture.getSubTexture(0, 0, 1, 0.5),
                    resourceTexture.getSubTexture(0, 0.5, 1, 0.5));
        } else {
            setTexture(buttonTexture, buttonTexture);
        }

        supplier(isPressedCondition);
    }

    public ToggleButtonComponent setShouldUseBaseBackground() {
        if (texture != null) {
            setTexture(
                    UITextures.group(GuiTextures.TOGGLE_BUTTON_BACK.getSubTexture(0, 0, 1, 0.5), texture),
                    UITextures.group(GuiTextures.TOGGLE_BUTTON_BACK.getSubTexture(0, 0.5, 1, 0.5), texture));
        }
        return this;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        if (tooltipText != null) {
            tooltip(List.of(Component.translatable(tooltipText + (pressed ? ".enabled" : ".disabled"))));
        }
    }
}
