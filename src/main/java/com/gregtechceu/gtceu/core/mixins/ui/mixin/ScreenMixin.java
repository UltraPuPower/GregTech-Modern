package com.gregtechceu.gtceu.core.mixins.ui.mixin;

import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.layers.Layer;
import com.gregtechceu.gtceu.api.ui.layers.Layers;
import com.gregtechceu.gtceu.api.ui.util.pond.UIScreenExtension;

import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(value = Screen.class, priority = 1100)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements UIScreenExtension {

    @Shadow
    public int width;
    @Shadow
    public int height;

    @Unique
    private final List<Layer<?, ?>.Instance> gtceu$instances = new ArrayList<>();
    @Unique
    private final List<Layer<?, ?>.Instance> gtceu$instancesView = Collections.unmodifiableList(this.gtceu$instances);
    @Unique
    private final Map<Layer<?, ?>, Layer<?, ?>.Instance> gtceu$layersToInstances = new HashMap<>();

    @Unique
    private boolean gtceu$layersInitialized = false;

    @Unique
    private Screen gtceu$this() {
        return (Screen) (Object) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void gtceu$updateLayers() {
        if (this.gtceu$layersInitialized) {
            for (var instance : this.gtceu$instances) {
                instance.resize(this.width, this.height);
            }
        } else {
            for (var layer : Layers.getLayers((Class<Screen>) this.gtceu$this().getClass())) {
                var instance = layer.instantiate(this.gtceu$this());
                this.gtceu$instances.add(instance);
                this.gtceu$layersToInstances.put(layer, instance);

                instance.adapter.inflateAndMount();
            }

            this.gtceu$layersInitialized = true;
        }

        this.gtceu$instances.forEach(Layer.Instance::dispatchLayoutUpdates);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends Screen, R extends ParentUIComponent> Layer<S, R>.Instance gtceu$getInstance(Layer<S, R> layer) {
        return (Layer<S, R>.Instance) this.gtceu$layersToInstances.get(layer);
    }

    @Override
    public List<Layer<?, ?>.Instance> gtceu$getInstancesView() {
        return this.gtceu$instancesView;
    }
}
