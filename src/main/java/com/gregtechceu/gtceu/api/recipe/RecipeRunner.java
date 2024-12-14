package com.gregtechceu.gtceu.api.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.chance.boost.ChanceBoostFunction;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.content.Content;

import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

/**
 * Used to handle recipes, only valid for a single RecipeCapability's entries
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
class RecipeRunner {

    record RecipeHandlingResult(@Nullable RecipeCapability<?> capability, @UnknownNullability List content,
                                RecipeHandler.ActionResult result) {}

    // --------------------------------------------------------------------------------------------------------

    private final GTRecipe recipe;
    private final IO io;
    private final boolean isTick;
    private final IRecipeCapabilityHolder holder;
    private final Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches;
    private final Map<IO, List<RecipeHandlerList>> capabilityProxies;
    private final boolean simulated;

    // These are only used to store mutable state during each invocation of handle()
    private RecipeCapability<?> capability;
    private Set<RecipeHandlerList> used;
    private Map<RecipeCapability<?>, List> recipeContents;
    private Map<RecipeCapability<?>, List> searchRecipeContents;
    /*@Getter
    private Map<IO, > contentMatchList;
    private @UnknownNullability List searchingMatchList;*/

    public RecipeRunner(GTRecipe recipe, IO io, boolean isTick,
                        IRecipeCapabilityHolder holder, Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches,
                        boolean simulated) {
        this.recipe = recipe;
        this.io = io;
        this.isTick = isTick;
        this.holder = holder;
        this.chanceCaches = chanceCaches;
        this.capabilityProxies = holder.getCapabilitiesProxy();
        this.recipeContents = new IdentityHashMap<>();
        this.searchRecipeContents = simulated ? recipeContents : new IdentityHashMap<>();
        this.simulated = simulated;
    }

    @Nullable
    public RecipeHandlingResult handle(Map<RecipeCapability<?>, List<Content>> entries) {
        initState();



        fillContentMatchList(entries);

        if(searchRecipeContents.isEmpty())
            return new RecipeHandlingResult(null, null, RecipeHandler.ActionResult.PASS_NO_CONTENTS);

        return this.handleContents();
    }

    private void initState() {
        used = new HashSet<>();
        //contentMatchList = new ArrayList<>();
        //searchingMatchList = simulated ? contentMatchList : new ArrayList<>();
    }

    /**
     * Populates the content match list to know if conditions are satisfied.
     */
    private void fillContentMatchList(Map<RecipeCapability<?>, List<Content>> entries) {
        ChanceBoostFunction function = recipe.getType().getChanceFunction();
        for(var entry : entries.entrySet()) {
            RecipeCapability<?> cap = entry.getKey();
            if (!cap.doMatchInRecipe()) {
                continue;
            }
            ChanceLogic logic = recipe.getChanceLogicForCapability(cap, this.io, this.isTick);
            List<Content> chancedContents = new ArrayList<>();
            if(entry.getValue().isEmpty()) continue;
            this.recipeContents.putIfAbsent(cap, new ArrayList<>());
            for (Content cont : entry.getValue()) {
                this.searchRecipeContents.computeIfAbsent(cap, c -> new ArrayList<>()).add(cont.content);

                // When simulating the recipe handling (used for recipe matching), chanced contents are ignored.
                if (simulated) continue;

                if (cont.chance >= cont.maxChance) {
                    this.recipeContents.get(cap).add(cont.content);
                } else {
                    chancedContents.add(cont);
                }
            }

            if (!chancedContents.isEmpty()) {
                int recipeTier = RecipeHelper.getPreOCRecipeEuTier(recipe);
                int chanceTier = recipeTier + recipe.ocLevel;
                var cache = this.chanceCaches.get(cap);
                chancedContents = logic.roll(chancedContents, function, recipeTier, chanceTier, cache, recipe.parallels);

                for (Content cont : chancedContents) {
                    this.recipeContents.get(cap).add(cont.content);
                }
            }

            if(!recipeContents.get(cap).isEmpty()) {}
                //recipeContents.put(cap, recipeContents.get(cap).stream().map(cap::copyContent).toList());
            else
                recipeContents.remove(cap);
        }
    }

    @Nullable
    private RecipeHandlingResult handleContents() {
        if(recipeContents.isEmpty()) {
            return new RecipeHandlingResult(null, null, RecipeHandler.ActionResult.SUCCESS);
        }
        var result = handleContentsInternal(io);
        if (!result.result.isSuccess()) {
            return result;
        }
        return handleContentsInternal(IO.BOTH);
    }

    private RecipeHandlingResult handleContentsInternal(IO capIO) {
        if(!capabilityProxies.containsKey(capIO))
            return new RecipeHandlingResult(null, null, RecipeHandler.ActionResult.SUCCESS);

        // noinspection DataFlowIssue checked above.
        var handlers = new ArrayList<>(capabilityProxies.get(capIO));
        List<RecipeHandlerList> distinct = new ArrayList<>(), nondistinct = new ArrayList<>();
        for(var handler : handlers) {
            if(handler.isDistinct())
                distinct.add(handler);
            else
                nondistinct.add(handler);
        }

        //handlers.sort(IRecipeHandler.ENTRY_COMPARATOR);

        // handle distinct first
        boolean handled = false;
        for (var handler : distinct) {
            var res = handler.handleRecipe(io, recipe, searchRecipeContents, true);
            if (res.isEmpty()) {
                if(!simulated) {
                    handler.handleRecipe(io, recipe, recipeContents, false);
                }
                handled = true;
                break;
            }
        }

        if(!handled) {
            for(var handler : nondistinct) {
                if(!recipeContents.isEmpty()) {
                    recipeContents = handler.handleRecipe(io, recipe, recipeContents, simulated);
                }
                if(recipeContents.isEmpty()) {
                    handled = true;
                    break;
                }
            }
        }

        if(!handled) {
            for (var handler : distinct) {
                if (!recipeContents.isEmpty()) {
                    var res = handler.handleRecipe(io, recipe, recipeContents, simulated);
                    if (res.isEmpty()) {
                        handled = true;
                        break;
                    }
                }
            }
        }

        if(handled) {
            return new RecipeHandlingResult(null, null, RecipeHandler.ActionResult.SUCCESS);
        }

        for(var entry : recipeContents.entrySet()) {
            if(entry.getValue() != null && !entry.getValue().isEmpty()) {
                return new RecipeHandlingResult(entry.getKey(), entry.getValue(), RecipeHandler.ActionResult.FAIL_NO_REASON);
            }
        }

        return new RecipeHandlingResult(null, null, RecipeHandler.ActionResult.FAIL_NO_REASON);
    }
}
