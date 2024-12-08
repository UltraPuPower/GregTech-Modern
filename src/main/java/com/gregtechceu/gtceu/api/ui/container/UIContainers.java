package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import net.minecraft.network.chat.Component;

public final class UIContainers {

    private UIContainers() {}

    // ------
    // Layout
    // ------

    public static RootContainer root(Sizing horizontalSizing, Sizing verticalSizing) {
        return new RootContainer(horizontalSizing, verticalSizing);
    }

    public static GridLayout grid(Sizing horizontalSizing, Sizing verticalSizing, int rows, int columns) {
        return new GridLayout(horizontalSizing, verticalSizing, rows, columns);
    }

    public static FlowLayout verticalFlow(Sizing horizontalSizing, Sizing verticalSizing) {
        return new FlowLayout(horizontalSizing, verticalSizing, FlowLayout.Algorithm.VERTICAL);
    }

    public static FlowLayout horizontalFlow(Sizing horizontalSizing, Sizing verticalSizing) {
        return new FlowLayout(horizontalSizing, verticalSizing, FlowLayout.Algorithm.HORIZONTAL);
    }

    public static FlowLayout ltrTextFlow(Sizing horizontalSizing, Sizing verticalSizing) {
        return new FlowLayout(horizontalSizing, verticalSizing, FlowLayout.Algorithm.LTR_TEXT);
    }

    public static StackLayout stack(Sizing horizontalSizing, Sizing verticalSizing) {
        return new StackLayout(horizontalSizing, verticalSizing);
    }

    // ------
    // Scroll
    // ------

    public static <C extends UIComponent> ScrollContainer<C> verticalScroll(Sizing horizontalSizing,
                                                                            Sizing verticalSizing, C child) {
        return new ScrollContainer<>(ScrollContainer.ScrollDirection.VERTICAL, horizontalSizing, verticalSizing, child);
    }

    public static <C extends UIComponent> ScrollContainer<C> horizontalScroll(Sizing horizontalSizing,
                                                                              Sizing verticalSizing, C child) {
        return new ScrollContainer<>(ScrollContainer.ScrollDirection.HORIZONTAL, horizontalSizing, verticalSizing,
                child);
    }

    // ----------------
    // Utility wrappers
    // ----------------

    public static <C extends UIComponent> DraggableContainer<C> draggable(Sizing horizontalSizing,
                                                                          Sizing verticalSizing, C child) {
        return new DraggableContainer<>(horizontalSizing, verticalSizing, child);
    }

    public static CollapsibleContainer collapsible(Sizing horizontalSizing, Sizing verticalSizing, Component title,
                                                   boolean expanded) {
        return new CollapsibleContainer(horizontalSizing, verticalSizing, title, expanded);
    }

    public static <C extends UIComponent> OverlayContainer<C> overlay(C child) {
        return new OverlayContainer<>(child);
    }

    public static <C extends UIComponent> RenderEffectWrapper<C> renderEffect(C child) {
        return new RenderEffectWrapper<>(child);
    }
}
