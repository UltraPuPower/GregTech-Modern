package com.gregtechceu.gtceu.common.cover.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.component.EnumSelectorComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import lombok.Getter;

public enum TransferMode implements EnumSelectorComponent.SelectableEnum {

    TRANSFER_ANY("cover.robotic_arm.transfer_mode.transfer_any", "transfer_any", 1),
    TRANSFER_EXACT("cover.robotic_arm.transfer_mode.transfer_exact", "transfer_exact", 1024),
    KEEP_EXACT("cover.robotic_arm.transfer_mode.keep_exact", "keep_exact", 1024);

    @Getter
    public final String tooltip;
    @Getter
    public final UITexture icon;
    public final int maxStackSize;

    TransferMode(String tooltip, String textureName, int maxStackSize) {
        this.tooltip = tooltip;
        this.maxStackSize = maxStackSize;
        this.icon = UITextures.resource(GTCEu.id("textures/gui/icon/transfer_mode/" + textureName + ".png"));
    }
}
