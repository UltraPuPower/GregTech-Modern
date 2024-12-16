package com.gregtechceu.gtceu.integration.jei.handler;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.ingredient.ClickableIngredientSlot;
import com.gregtechceu.gtceu.api.ui.ingredient.GhostIngredientSlot;
import com.gregtechceu.gtceu.integration.jei.GTJEIPlugin;

import net.minecraft.client.renderer.Rect2i;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UIJEIHandler implements IGuiContainerHandler<BaseContainerScreen<?, ?>>,
                          IGhostIngredientHandler<BaseContainerScreen<?, ?>> {

    public static final UIJEIHandler INSTANCE = new UIJEIHandler();

    private UIJEIHandler() {}

    @Override
    public List<Rect2i> getGuiExtraAreas(BaseContainerScreen<?, ?> screen) {
        return screen.componentsForExclusionAreas()
                .map(PositionedRectangle::area)
                .toList();
    }

    @Override
    public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(@NotNull BaseContainerScreen<?, ?> gui,
                                                                                       double mouseX, double mouseY) {
        List<ClickableIngredientSlot<?>> ghostSlots = gui.componentsForClickableIngredients().toList();
        for (var slot : ghostSlots) {
            if (slot.containerAccess() == null || !slot.enabled()) {
                continue;
            }
            Optional<? extends ITypedIngredient<?>> ingredient = GTJEIPlugin.JEI_RUNTIME
                    .getIngredientManager()
                    .createTypedIngredient(slot.getIngredients());
            return ingredient.map(i -> new ClickableIngredient<>(i, slot.area()));
        }
        return Optional.empty();
    }

    @Override
    public <I> List<Target<I>> getTargetsTyped(BaseContainerScreen<?, ?> gui, ITypedIngredient<I> ingredient,
                                               boolean doStart) {
        List<GhostIngredientSlot<?>> ghostSlots = gui.componentsForGhostIngredients().toList();
        List<Target<I>> ghostHandlerTargets = new ArrayList<>();
        for (var slot : ghostSlots) {
            if (slot.containerAccess() == null || !slot.enabled()) {
                continue;
            }

            if (slot.castGhostIngredientIfValid(ingredient.getIngredient()) != null) {
                @SuppressWarnings("unchecked")
                GhostIngredientSlot<I> slotWithType = (GhostIngredientSlot<I>) slot;
                ghostHandlerTargets.add(new GhostIngredientTarget<>(slotWithType));
            }
        }
        return ghostHandlerTargets;
    }

    @Override
    public void onComplete() {}

    private record ClickableIngredient<T>(ITypedIngredient<T> ingredient, Rect2i area)
            implements IClickableIngredient<T> {

        @SuppressWarnings("removal") // I have to override this.
        @Override
        public ITypedIngredient<T> getTypedIngredient() {
            return ingredient;
        }

        @Override
        public IIngredientType<T> getIngredientType() {
            return ingredient.getType();
        }

        @Override
        public T getIngredient() {
            return ingredient.getIngredient();
        }

        @Override
        public Rect2i getArea() {
            return area;
        }
    }
}
