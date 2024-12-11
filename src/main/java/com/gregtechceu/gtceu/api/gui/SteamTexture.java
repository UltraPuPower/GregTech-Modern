package com.gregtechceu.gtceu.api.gui;

import com.gregtechceu.gtceu.api.ui.texture.ResourceTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import net.minecraft.resources.ResourceLocation;

/**
 * @author KilaBash
 * @date 2023/2/22
 * @implNote SteamTexture
 */
public class SteamTexture {

    private static final String BRONZE = "bronze";
    private static final String STEEL = "steel";

    private final ResourceTexture bronzeTexture;
    private final ResourceTexture steelTexture;

    private SteamTexture(ResourceTexture bronzeTexture, ResourceTexture steelTexture) {
        this.bronzeTexture = bronzeTexture;
        this.steelTexture = steelTexture;
    }

    public static SteamTexture fullImage(ResourceLocation path) {
        return new SteamTexture(
                UITextures.resource(new ResourceLocation(path.getNamespace(), String.format(path.getPath(), BRONZE))),
                UITextures.resource(new ResourceLocation(path.getNamespace(), String.format(path.getPath(), STEEL))));
    }

    public ResourceTexture get(boolean isHighPressure) {
        return isHighPressure ? steelTexture : bronzeTexture;
    }
}
