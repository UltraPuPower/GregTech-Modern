package com.gregtechceu.gtceu.api.ui.texture;

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

    public static SteamTexture fullImage(String path) {
        return new SteamTexture(
                UITextures.resource(new ResourceLocation(String.format(path, BRONZE))),
                UITextures.resource(new ResourceLocation(String.format(path, STEEL))));
    }

    public ResourceTexture get(boolean isHighPressure) {
        return isHighPressure ? steelTexture : bronzeTexture;
    }
}
