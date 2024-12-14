package com.gregtechceu.gtceu.integration.jei;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.ingredient.GhostIngredientSlot;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

public class GhostIngredientTarget<I> implements IGhostIngredientHandler.Target<I> {

    private final GhostIngredientSlot<I> ghostSlot;

    public static <I> GhostIngredientTarget<I> of(GhostIngredientSlot<I> slot) {
        return new GhostIngredientTarget<>(slot);
    }

    public static <I, W extends UIComponent & GhostIngredientSlot<I>> GhostIngredientTarget<I> of(W slot) {
        return new GhostIngredientTarget<>(slot);
    }

    public GhostIngredientTarget(GhostIngredientSlot<I> ghostSlot) {
        this.ghostSlot = ghostSlot;
    }

    @Override
    public @NotNull Rect2i getArea() {
        return this.ghostSlot.area();
    }

    @Override
    public void accept(@NotNull I ingredient) {
        if (this.ghostSlot.ingredientHandlingOverride(ingredient)) {
            return;
        }
        ingredient = this.ghostSlot.castGhostIngredientIfValid(ingredient);
        if (ingredient == null) {
            throw new IllegalStateException("Ghost slot did accept ingredient before, but now it doesn't.");
        }
        this.ghostSlot.setGhostIngredient(ingredient);
    }
}
