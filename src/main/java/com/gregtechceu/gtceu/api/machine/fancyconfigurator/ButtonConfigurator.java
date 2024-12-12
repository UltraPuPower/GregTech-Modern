package com.gregtechceu.gtceu.api.machine.fancyconfigurator;

import com.gregtechceu.gtceu.api.ui.component.ButtonComponent;
import com.gregtechceu.gtceu.api.ui.fancy.IFancyConfiguratorButton;

import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;

import net.minecraft.network.chat.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Accessors(chain = true)
public class ButtonConfigurator implements IFancyConfiguratorButton {

    @Getter
    protected UITexture icon;

    protected Consumer<ButtonComponent> onClick;

    @Getter
    @Setter
    protected List<Component> tooltips = Collections.emptyList();

    public ButtonConfigurator(UITexture texture, Consumer<ButtonComponent> onClick) {
        this.icon = texture;
        this.onClick = onClick;
    }

    @Override
    public void onClick(ButtonComponent clickData) {
        onClick.accept(clickData);
    }
}
