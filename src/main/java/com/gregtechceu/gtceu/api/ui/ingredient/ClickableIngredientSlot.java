package com.gregtechceu.gtceu.api.ui.ingredient;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.integration.xei.entry.EntryList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.UnaryOperator;

/**
 * An interface for compat with recipe viewers' item selectors.
 * Implement this on any {@link UIComponent}.
 *
 * @param <I> type of the ingredient
 */
public interface ClickableIngredientSlot<I> extends UIComponent {

    @UnknownNullability("Nullability depends on the type of ingredient")
    EntryList<I> getIngredients();

    default UnaryOperator<I> renderMappingFunction() {
        return UnaryOperator.identity();
    }

    default float chance() {
        return 1.0f;
    }

    default IO ingredientIO() {
        return null;
    }

    /**
     * @return the class of the ingredient this slot contains
     */
    @NotNull
    Class<I> ingredientClass();

    /**
     * @return a recipeviewer-specific ingredient instance, or {@code null} if defaulting to the class-based logic.
     */
    @Nullable
    default Object ingredientOverride() {
        return null;
    }
}
