package com.gregtechceu.gtceu.api.ui.layers;

import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.util.pond.UIScreenExtension;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A system for adding owo-ui components onto existing screens.
 * <p>
 * You can create a new layer by calling {@link #add(BiFunction, Consumer, Class[])}. The
 * second argument to this function is the instance initializer, which is where you configure
 * instances of your layer added onto screens when they get initialized. This is the place to
 * configure the UI adapter of your layer as well as building your UI tree onto the root
 * component of said adapter
 * <p>
 * Just like proper owo-ui screens, layers preserve state when the client's window
 * is resized - they are only initialized once, when the screen is first opened
 */
public final class Layers {

    private static final Multimap<Class<? extends Screen>, Layer<?, ?>> LAYERS = HashMultimap.create();

    /**
     * Add a new layer to the given screens
     *
     * @param rootComponentMaker  A function which will create the root component of this layer
     * @param instanceInitializer A function which will initialize any instances of this layer which get created.
     *                            This is where you add components or configure the UI adapter of the generated layer
     *                            instance
     * @param screenClasses       The screens onto which to add the new layer
     */
    @SafeVarargs
    public static <S extends Screen,
            R extends ParentUIComponent> Layer<S, R> add(BiFunction<Sizing, Sizing, R> rootComponentMaker,
                                                         Consumer<Layer<S, R>.Instance> instanceInitializer,
                                                         Class<? extends S>... screenClasses) {
        final var layer = new Layer<S, R>(rootComponentMaker, instanceInitializer);
        for (var screenClass : screenClasses) {
            LAYERS.put(screenClass, layer);
        }
        return layer;
    }

    /**
     * Get all layers associated with a given screen
     */
    @SuppressWarnings("unchecked")
    public static <S extends Screen> Collection<Layer<S, ?>> getLayers(Class<S> screenClass) {
        return (Collection<Layer<S, ?>>) (Object) LAYERS.get(screenClass);
    }

    /**
     * Get all layer instances currently present on the given screen
     */
    @SuppressWarnings("unchecked")
    public static <S extends Screen> List<Layer<S, ?>.Instance> getInstances(S screen) {
        return (List<Layer<S, ?>.Instance>) (Object) ((UIScreenExtension) screen).gtceu$getInstancesView();
    }

    static {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, Layers::onScreenOpening);
        MinecraftForge.EVENT_BUS.addListener(Layers::onScreenClosing);
        MinecraftForge.EVENT_BUS.addListener(Layers::onScreenRenderPre);
        MinecraftForge.EVENT_BUS.addListener(Layers::onScreenRenderPost);
        MinecraftForge.EVENT_BUS.addListener(Layers::onMouseButtonPressedPre);
        MinecraftForge.EVENT_BUS.addListener(Layers::onMouseButtonReleasedPre);
        MinecraftForge.EVENT_BUS.addListener(Layers::onMouseButtonScrolledPre);
        MinecraftForge.EVENT_BUS.addListener(Layers::onKeyPressedPre);
        MinecraftForge.EVENT_BUS.addListener(Layers::onKeyReleasedPre);
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() == null) {
            return;
        }
        ((UIScreenExtension) event.getNewScreen()).gtceu$updateLayers();
    }

    private static void onScreenClosing(ScreenEvent.Closing event) {
        for (var instance : getInstances(event.getScreen())) {
            instance.adapter.dispose();
        }
    }

    private static void onScreenRenderPre(ScreenEvent.Render.Pre event) {
        for (var instance : getInstances(event.getScreen())) {
            if (instance.aggressivePositioning) instance.dispatchLayoutUpdates();
        }
    }

    private static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        event.getGuiGraphics().flush();
        for (var instance : getInstances(event.getScreen())) {
            if (instance.aggressivePositioning) instance.dispatchLayoutUpdates();
        }
    }

    private static void onMouseButtonPressedPre(ScreenEvent.MouseButtonPressed.Pre event) {
        for (var instance : getInstances(event.getScreen())) {
            if (instance.adapter.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
                event.setCanceled(true);
            }
        }
    }

    private static void onMouseButtonReleasedPre(ScreenEvent.MouseButtonReleased.Pre event) {
        for (var instance : getInstances(event.getScreen())) {
            if (instance.adapter.mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton())) {
                event.setCanceled(true);
            }
        }
    }

    private static void onMouseButtonScrolledPre(ScreenEvent.MouseScrolled.Pre event) {
        for (var instance : getInstances(event.getScreen())) {
            if (instance.adapter.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta())) {
                event.setCanceled(true);
            }
        }
    }

    private static void onKeyPressedPre(ScreenEvent.KeyPressed.Pre event) {
        for (var instance : getInstances(event.getScreen())) {
            if (instance.adapter.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
                event.setCanceled(true);
            }
        }
    }

    private static void onKeyReleasedPre(ScreenEvent.KeyReleased.Pre event) {
        for (var instance : getInstances(event.getScreen())) {
            if (instance.adapter.keyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
                event.setCanceled(true);
            }
        }
    }
}
