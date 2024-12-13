package com.gregtechceu.gtceu.api.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class RecipeHandler {

    ///////////////////////////////////////////////////////////////
    // **********************internal logic********************* //
    ///////////////////////////////////////////////////////////////

    public static ActionResult matchRecipe(IRecipeCapabilityHolder holder, GTRecipe recipe) {
        return matchRecipe(holder, recipe, false);
    }

    public static ActionResult matchTickRecipe(IRecipeCapabilityHolder holder, GTRecipe recipe) {
        return recipe.hasTick() ? matchRecipe(holder, recipe, true) : ActionResult.SUCCESS;
    }

    private static ActionResult matchRecipe(IRecipeCapabilityHolder holder, GTRecipe recipe, boolean tick) {
        if (!holder.hasCapabilityProxies())
            return ActionResult.FAIL_NO_CAPABILITIES;

        var result = handleRecipe(IO.IN, holder, recipe, tick ? recipe.tickInputs : recipe.inputs,
                Collections.emptyMap(), tick, true);
        if (!result.isSuccess()) return result;

        result = handleRecipe(IO.OUT, holder, recipe, tick ? recipe.tickOutputs : recipe.outputs,
                Collections.emptyMap(), tick, true);
        return result;
    }

    public static ActionResult handleRecipeIO(IO io, IRecipeCapabilityHolder holder, GTRecipe recipe,
                                              Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches) {
        if (!holder.hasCapabilityProxies() || io == IO.BOTH)
            return ActionResult.FAIL_NO_CAPABILITIES;
        return handleRecipe(io, holder, recipe, io == IO.IN ? recipe.inputs : recipe.outputs, chanceCaches, false,
                false);
    }

    public static ActionResult handleTickRecipeIO(IO io, IRecipeCapabilityHolder holder, GTRecipe recipe,
                                                  Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches) {
        if (!holder.hasCapabilityProxies() || io == IO.BOTH)
            return ActionResult.FAIL_NO_CAPABILITIES;
        return handleRecipe(io, holder, recipe, io == IO.IN ? recipe.tickInputs : recipe.tickOutputs, chanceCaches,
                true, false);
    }

    /**
     * Checks if all the contents of the recipe are located in the holder.
     *
     * @param isTick
     * @param simulated checks that the recipe ingredients are in the holder if true,
     *                  process the recipe contents if false
     */
    public static ActionResult handleRecipe(IO io, IRecipeCapabilityHolder holder, GTRecipe recipe,
                                            Map<RecipeCapability<?>, List<Content>> contents,
                                            Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches,
                                            boolean isTick, boolean simulated) {
        RecipeRunner runner = new RecipeRunner(recipe, io, isTick, holder, chanceCaches, simulated);
        var handle = runner.handle(contents);

        if (handle == null || handle.content() != null) {
            String key = "gtceu.recipe_logic.insufficient_" + (io == IO.IN ? "in" : "out");
            return ActionResult.fail(() -> Component.translatable(key)
                    .append(": ").append(handle.capability().getName()));
        }
        return handle.result();

        /*
         * RecipeRunner runner = new RecipeRunner(recipe, io, isTick, holder, chanceCaches, false);
         * for (Map.Entry<RecipeCapability<?>, List<Content>> entry : contents.entrySet()) {
         * var handled = runner.handle(entry);
         * if (handled == null)
         * continue;
         * 
         * if (handled.content() != null) {
         * GTCEu.LOGGER.warn("io error while handling a recipe {} outputs. holder: {}", recipe.id, holder);
         * return false;
         * }
         * }
         * return true;
         */
    }

    public static ActionResult matchContents(IRecipeCapabilityHolder holder, GTRecipe recipe) {
        var match = matchRecipe(holder, recipe);
        if (!match.isSuccess)
            return match;
        return matchTickRecipe(holder, recipe);
    }

    public static void preWorking(IRecipeCapabilityHolder holder, GTRecipe recipe) {
        handlePre(holder, recipe, IO.IN);
        handlePre(holder, recipe, IO.OUT);
    }

    public static void postWorking(IRecipeCapabilityHolder holder, GTRecipe recipe) {
        handlePost(holder, recipe, IO.IN);
        handlePost(holder, recipe, IO.OUT);
    }

    public static void handlePre(IRecipeCapabilityHolder holder, GTRecipe recipe, IO io) {
        (io == io.IN ? recipe.inputs : recipe.outputs).forEach(((capability, tuples) -> {
            var capFlatMap = holder.getCapabilitiesFlat(io, capability);
            if (!capFlatMap.isEmpty()) {
                for (IRecipeHandler<?> capabilityProxy : capFlatMap) {
                    capabilityProxy.preWorking(holder, io, recipe);
                }
            } else if (!holder.getCapabilitiesFlat(IO.BOTH, capability).isEmpty()) {
                for (IRecipeHandler<?> capabilityProxy : holder.getCapabilitiesFlat(IO.BOTH, capability)) {
                    capabilityProxy.preWorking(holder, io, recipe);
                }
            }
        }));
    }

    public static void handlePost(IRecipeCapabilityHolder holder, GTRecipe recipe, IO io) {
        (io == io.IN ? recipe.inputs : recipe.outputs).forEach(((capability, tuples) -> {
            var capFlatMap = holder.getCapabilitiesFlat(io, capability);
            if (!capFlatMap.isEmpty()) {
                for (IRecipeHandler<?> capabilityProxy : capFlatMap) {
                    capabilityProxy.postWorking(holder, io, recipe);
                }
            } else if (!holder.getCapabilitiesFlat(IO.BOTH, capability).isEmpty()) {
                for (IRecipeHandler<?> capabilityProxy : holder.getCapabilitiesFlat(IO.BOTH, capability)) {
                    capabilityProxy.postWorking(holder, io, recipe);
                }
            }
        }));
    }

    /**
     * Check whether all conditions of a recipe are valid
     * 
     * @param recipe      the recipe to test
     * @param recipeLogic the logic to test against the conditions
     * @return the list of failed conditions, or success if all conditions are satisfied
     */
    public static List<ActionResult> checkConditions(GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        if (recipe.conditions.isEmpty()) return List.of(ActionResult.SUCCESS);
        Map<RecipeConditionType<?>, List<RecipeCondition>> or = new HashMap<>();
        List<ActionResult> failures = new ArrayList<>();
        for (RecipeCondition condition : recipe.conditions) {
            if (condition.isOr()) {
                or.computeIfAbsent(condition.getType(), type -> new ArrayList<>()).add(condition);
            } else if (condition.test(recipe, recipeLogic) == condition.isReverse()) {
                failures.add(ActionResult
                        .fail(() -> Component.translatable("gtceu.recipe_logic.condition_fails").append(": ")
                                .append(condition.getTooltips())));
            }
        }

        for (List<RecipeCondition> conditions : or.values()) {
            if (conditions.stream()
                    .allMatch(condition -> condition.test(recipe, recipeLogic) == condition.isReverse())) {
                failures.add(ActionResult.fail(() -> Component.translatable("gtceu.recipe_logic.condition_fails")));
            }
        }
        if (!failures.isEmpty())
            return failures;
        return List.of(ActionResult.SUCCESS);
    }

    /**
     * Trims the recipe outputs and tick outputs based on the performing Machine's trim limit.
     */
    public static GTRecipe trimRecipeOutputs(GTRecipe recipe, Map<RecipeCapability<?>, Integer> trimLimits) {
        // Fast return early if no trimming desired
        if (trimLimits.isEmpty() || trimLimits.values().stream().allMatch(integer -> integer == -1)) {
            return recipe;
        }

        GTRecipe current = recipe.copy();

        GTRecipeBuilder builder = new GTRecipeBuilder(current, recipe.recipeType);

        builder.output.clear();
        builder.tickOutput.clear();

        Map<RecipeCapability<?>, List<Content>> recipeOutputs = doTrim(current.outputs, trimLimits);
        Map<RecipeCapability<?>, List<Content>> recipeTickOutputs = doTrim(current.tickOutputs, trimLimits);

        builder.output.putAll(recipeOutputs);
        builder.tickOutput.putAll(recipeTickOutputs);

        return builder.buildRawRecipe();
    }

    /**
     * Returns the maximum possible recipe outputs from a recipe, divided into regular and chanced outputs
     * Takes into account any specific output limiters, ie macerator slots, to trim down the output list
     * Trims from chanced outputs first, then regular outputs
     *
     * @param trimLimits The limit(s) on the number of outputs, -1 for disabled.
     * @return All recipe outputs, limited by some factor(s)
     */
    public static Map<RecipeCapability<?>, List<Content>> doTrim(Map<RecipeCapability<?>, List<Content>> current,
                                                                 Map<RecipeCapability<?>, Integer> trimLimits) {
        Map<RecipeCapability<?>, List<Content>> outputs = new HashMap<>();

        Set<RecipeCapability<?>> trimmed = new HashSet<>();
        for (Map.Entry<RecipeCapability<?>, Integer> entry : trimLimits.entrySet()) {
            RecipeCapability<?> key = entry.getKey();

            if (!current.containsKey(key)) continue;
            List<Content> nonChanced = new ArrayList<>();
            List<Content> chanced = new ArrayList<>();
            for (Content content : current.getOrDefault(key, List.of())) {
                if (content.chance <= 0 || content.chance >= content.maxChance) nonChanced.add(content);
                else chanced.add(content);
            }

            int outputLimit = entry.getValue();
            if (outputLimit == -1) {
                outputs.computeIfAbsent(key, $ -> new ArrayList<>()).addAll(nonChanced);
            }
            // If just the regular outputs would satisfy the outputLimit
            else if (nonChanced.size() >= outputLimit) {
                outputs.computeIfAbsent(key, $ -> new ArrayList<>())
                        .addAll(nonChanced.stream()
                                .map(cont -> cont.copy(key, null))
                                .toList()
                                .subList(0, outputLimit));

                chanced.clear();
            }
            // If the regular outputs and chanced outputs are required to satisfy the outputLimit
            else if (!nonChanced.isEmpty() && (nonChanced.size() + chanced.size()) >= outputLimit) {
                outputs.computeIfAbsent(key, $ -> new ArrayList<>())
                        .addAll(nonChanced.stream().map(cont -> cont.copy(key, null)).toList());

                // Calculate the number of chanced outputs after adding all the regular outputs
                int numChanced = outputLimit - nonChanced.size();

                chanced = chanced.subList(0, Math.min(numChanced, chanced.size()));
            }
            // There are only chanced outputs to satisfy the outputLimit
            else if (nonChanced.isEmpty()) {
                chanced = chanced.subList(0, Math.min(outputLimit, chanced.size()));
            }
            // The number of outputs + chanced outputs is lower than the trim number, so just add everything
            else {
                outputs.computeIfAbsent(key, $ -> new ArrayList<>())
                        .addAll(nonChanced.stream().map(cont -> cont.copy(key, null)).toList());
                // Chanced outputs are taken care of in the original copy
            }

            if (!chanced.isEmpty())
                outputs.computeIfAbsent(key, $ -> new ArrayList<>())
                        .addAll(chanced.stream().map(cont -> cont.copy(key, null)).toList());

            trimmed.add(key);
        }
        for (Map.Entry<RecipeCapability<?>, List<Content>> entry : current.entrySet()) {
            if (trimmed.contains(entry.getKey())) continue;
            outputs.computeIfAbsent(entry.getKey(), $ -> new ArrayList<>()).addAll(entry.getValue());
        }

        return outputs;
    }

    public static List<ActionResult> checkRecipeValidity(GTRecipe recipe) {
        List<ActionResult> results = new ArrayList<>();
        var result = checkItemValid(recipe.inputs, "input");
        if (result != ActionResult.SUCCESS) {
            results.add(result);
        }
        result = checkItemValid(recipe.outputs, "output");
        if (result != ActionResult.SUCCESS) {
            results.add(result);
        }
        result = checkItemValid(recipe.tickInputs, "tickInput");
        if (result != ActionResult.SUCCESS) {
            results.add(result);
        }
        result = checkItemValid(recipe.outputs, "tickOutput");
        if (result != ActionResult.SUCCESS) {
            results.add(result);
        }
        if (!results.isEmpty())
            return results;
        return List.of(ActionResult.SUCCESS);
    }

    private static ActionResult checkItemValid(Map<RecipeCapability<?>, List<Content>> contents, String name) {
        for (Content content : contents.getOrDefault(ItemRecipeCapability.CAP, Collections.emptyList())) {
            var items = ItemRecipeCapability.CAP.of(content.content).getItems();
            if (items.length == 0) {
                return ActionResult
                        .fail(() -> Component.translatable("gtceu.recipe_logic.no_" + name + "_ingredients"));
            } else if (Arrays.stream(items).anyMatch(ItemStack::isEmpty)) {
                return ActionResult.fail(() -> Component.translatable("gtceu.recipe_logic.invalid_stack"));
            }
        }
        return ActionResult.SUCCESS;
    }

    private static ActionResult checkFluidValid(Map<RecipeCapability<?>, List<Content>> contents, String name) {
        for (Content content : contents.getOrDefault(ItemRecipeCapability.CAP, Collections.emptyList())) {
            var fluids = FluidRecipeCapability.CAP.of(content.content).getStacks();
            if (fluids.length == 0) {
                return ActionResult
                        .fail(() -> Component.translatable("gtceu.recipe_logic.no_" + name + "_ingredients"));
            } else if (Arrays.stream(fluids).anyMatch(FluidStack::isEmpty)) {
                return ActionResult.fail(() -> Component.translatable("gtceu.recipe_logic.invalid_stack"));
            }
        }
        return ActionResult.SUCCESS;
    }

    /**
     * @param isSuccess is action success
     * @param reason    if fail, fail reason
     */
    public record ActionResult(boolean isSuccess, @Nullable Supplier<Component> reason) {

        public final static ActionResult SUCCESS = new ActionResult(true, null);
        public final static ActionResult FAIL_NO_REASON = new ActionResult(false, null);
        public final static ActionResult PASS_NO_CONTENTS = new ActionResult(true, () -> Component.translatable("gtceu.recipe_logic.no_contents"));
        public final static ActionResult FAIL_NO_CAPABILITIES = new ActionResult(false, () -> Component.translatable("gtceu.recipe_logic.no_capabilities"));

        public static ActionResult fail(@Nullable Supplier<Component> component) {
            return new ActionResult(false, component);
        }

        public Component getReason() {
            if (reason() == null) {
                return Component.empty();
            }
            return reason().get();
        }
    }
}
