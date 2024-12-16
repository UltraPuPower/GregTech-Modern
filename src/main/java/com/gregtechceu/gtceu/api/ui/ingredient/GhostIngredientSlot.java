package com.gregtechceu.gtceu.api.ui.ingredient;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for compat with recipe viewers' ghost slots.
 * Implement this on any {@link UIComponent}.
 *
 * @param <I> type of the ingredient
 */
public interface GhostIngredientSlot<I> extends UIComponent {

    /**
     * Puts the ingredient in this ghost slot.
     * Was cast with {@link #castGhostIngredientIfValid(Object)}.
     *
     * @param ingredient ingredient to put
     */
    void setGhostIngredient(@NotNull I ingredient);

    /**
     * Tries to cast an ingredient to the type of this slot.
     * Returns null if the ingredient can't be cast.
     * Must be consistent.
     *
     * @param ingredient ingredient to cast
     * @return cast ingredient or null
     */
    @Nullable
    default I castGhostIngredientIfValid(@NotNull Object ingredient) {
        // noinspection unchecked
        return ghostIngredientClass().isInstance(ingredient) ? (I) ingredient : null;
    }

    /**
     * @return the class of the ingredient this slot expects
     */
    Class<I> ghostIngredientClass();

    /**
     * A way to handle recipeviewer-specific ingredient instances.
     * 
     * @return {@code true} if handling the ingredient yourself.
     */
    default boolean ingredientHandlingOverride(Object ingredient) {
        return false;
    }
}
