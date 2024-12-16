package com.gregtechceu.gtceu.common.cover.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.component.EnumSelectorComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import lombok.Getter;

public enum VoidingMode implements EnumSelectorComponent.SelectableEnum {

    VOID_ANY("cover.voiding.voiding_mode.void_any", "void_any", 1),
    VOID_OVERFLOW("cover.voiding.voiding_mode.void_overflow", "void_overflow", 1024);

    @Getter
    public final String tooltip;
    @Getter
    public final UITexture icon;
    public final int maxStackSize;

    VoidingMode(String tooltip, String textureName, int maxStackSize) {
        this.tooltip = tooltip;
        this.maxStackSize = maxStackSize;
        this.icon = UITextures.resource(GTCEu.id("textures/gui/icon/voiding_mode/" + textureName + ".png"));
    }
}
