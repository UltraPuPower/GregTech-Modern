package com.gregtechceu.gtceu.api.capability.recipe;

import com.gregtechceu.gtceu.GTCEu;

import com.gregtechceu.gtceu.api.ui.component.EnumSelectorComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import lombok.Getter;

/**
 * The capability can be input or output or both
 * <p/>
 * Also used in recipe viewer compatibility, where {@code NONE} signifies a catalyst (e.g. unconsumed ingredient)
 * and {@code null} is "do not add".
 */
public enum IO implements EnumSelectorComponent.SelectableEnum {

    IN("gtceu.io.import", "import"),
    OUT("gtceu.io.export", "export"),
    BOTH("gtceu.io.both", "both"),
    NONE("gtceu.io.none", "none");

    @Getter
    public final String tooltip;
    @Getter
    public final UITexture icon;

    IO(String tooltip, String textureName) {
        this.tooltip = tooltip;
        this.icon = UITextures.resource(GTCEu.id("textures/gui/icon/io_mode/" + textureName + ".png"));
    }

    public boolean support(IO io) {
        if (io == this) return true;
        if (io == NONE) return false;
        return this == BOTH;
    }
}
