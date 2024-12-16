package com.gregtechceu.gtceu.common.cover.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.component.EnumSelectorComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

public enum DistributionMode implements EnumSelectorComponent.SelectableEnum {

    ROUND_ROBIN_GLOBAL("round_robin_global"),
    ROUND_ROBIN_PRIO("round_robin_prio"),
    INSERT_FIRST("insert_first");

    public static final DistributionMode[] VALUES = values();
    private static final float OFFSET = 1.0f / VALUES.length;

    public final String localeName;

    DistributionMode(String localeName) {
        this.localeName = localeName;
    }

    @Override
    public String getTooltip() {
        return "cover.conveyor.distribution." + localeName;
    }

    @Override
    public UITexture getIcon() {
        return UITextures.resource(GTCEu.id("textures/gui/icon/distribution_mode/" + localeName + ".png"));
    }
}
