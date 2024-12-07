package com.gregtechceu.gtceu.api.ui.util.pond;

import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.layers.Layer;

import net.minecraft.client.gui.screens.Screen;

import java.util.List;

public interface UIScreenExtension {

    List<Layer<?, ?>.Instance> gtceu$getInstancesView();

    <S extends Screen, R extends ParentUIComponent> Layer<S, R>.Instance gtceu$getInstance(Layer<S, R> layer);

    void gtceu$updateLayers();
}
