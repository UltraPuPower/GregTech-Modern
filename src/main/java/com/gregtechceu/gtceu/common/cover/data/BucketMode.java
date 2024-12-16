package com.gregtechceu.gtceu.common.cover.data;

import com.gregtechceu.gtceu.api.ui.component.EnumSelectorComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import net.minecraft.resources.ResourceLocation;

import lombok.Getter;

public enum BucketMode implements EnumSelectorComponent.SelectableEnum {

    BUCKET("cover.bucket.mode.bucket", "minecraft:textures/item/water_bucket", 1000),
    MILLI_BUCKET("cover.bucket.mode.milli_bucket", "gtceu:textures/gui/icon/bucket_mode/water_drop", 1);

    @Getter
    public final String tooltip;
    @Getter
    public final UITexture icon;

    public final int multiplier;

    BucketMode(String tooltip, String textureName, int multiplier) {
        this.tooltip = tooltip;
        this.icon = UITextures.resource(new ResourceLocation(textureName + ".png")).scale(16F / 20F);
        this.multiplier = multiplier;
    }
}
