package com.gregtechceu.gtceu.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface GTResourceReloadListener extends PreparableReloadListener {

    ResourceLocation getId();
}
