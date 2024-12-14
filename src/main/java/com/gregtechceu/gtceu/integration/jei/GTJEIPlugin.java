package com.gregtechceu.gtceu.integration.jei;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.jei.circuit.GTProgrammedCircuitCategory;
import com.gregtechceu.gtceu.integration.jei.multipage.MultiblockInfoCategory;
import com.gregtechceu.gtceu.integration.jei.oreprocessing.GTOreProcessingInfoCategory;
import com.gregtechceu.gtceu.integration.jei.orevein.GTBedrockFluidInfoCategory;
import com.gregtechceu.gtceu.integration.jei.orevein.GTBedrockOreInfoCategory;
import com.gregtechceu.gtceu.integration.jei.orevein.GTOreVeinInfoCategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/2/25
 * @implNote JEIPlugin
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@JeiPlugin
public class GTJEIPlugin implements IModPlugin {

    public static IJeiRuntime JEI_RUNTIME;

    @Override
    public ResourceLocation getPluginUid() {
        return GTCEu.id("jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registry) {
        if (LDLib.isReiLoaded() || LDLib.isEmiLoaded()) return;
        GTCEu.LOGGER.info("JEI register categories");
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new MultiblockInfoCategory(jeiHelpers));
        if (!ConfigHolder.INSTANCE.compat.hideOreProcessingDiagrams)
            registry.addRecipeCategories(new GTOreProcessingInfoCategory(jeiHelpers));
        registry.addRecipeCategories(new GTOreVeinInfoCategory(jeiHelpers));
        registry.addRecipeCategories(new GTBedrockFluidInfoCategory(jeiHelpers));
        if (ConfigHolder.INSTANCE.machines.doBedrockOres)
            registry.addRecipeCategories(new GTBedrockOreInfoCategory(jeiHelpers));
        for (GTRecipeCategory category : GTRegistries.RECIPE_CATEGORIES) {
            if (Platform.isDevEnv() || category.isXEIVisible()) {
                registry.addRecipeCategories(new GTRecipeJEICategory(jeiHelpers, category));
            }
        }
        registry.addRecipeCategories(new GTProgrammedCircuitCategory(jeiHelpers));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(BaseContainerScreen.class, UIJEIHandler.INSTANCE);
        // why is there no generic version for this.>
        //noinspection unchecked,rawtypes
        registration.addGhostIngredientHandler(BaseContainerScreen.class,
                (IGhostIngredientHandler<BaseContainerScreen>) (IGhostIngredientHandler<?>) UIJEIHandler.INSTANCE);
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        if (LDLib.isReiLoaded() || LDLib.isEmiLoaded()) return;
        GTRecipeJEICategory.registerRecipeCatalysts(registration);
        if (!ConfigHolder.INSTANCE.compat.hideOreProcessingDiagrams)
            GTOreProcessingInfoCategory.registerRecipeCatalysts(registration);
        GTOreVeinInfoCategory.registerRecipeCatalysts(registration);
        GTBedrockFluidInfoCategory.registerRecipeCatalysts(registration);
        if (ConfigHolder.INSTANCE.machines.doBedrockOres)
            GTBedrockOreInfoCategory.registerRecipeCatalysts(registration);
        registration.addRecipeCatalyst(GTMachines.LARGE_CHEMICAL_REACTOR.asStack(),
                GTRecipeJEICategory.TYPES.apply(GTRecipeTypes.CHEMICAL_RECIPES.getCategory()));
        registration.addRecipeCatalyst(IntCircuitBehaviour.stack(0), GTProgrammedCircuitCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (LDLib.isReiLoaded() || LDLib.isEmiLoaded()) return;
        GTCEu.LOGGER.info("JEI register");
        MultiblockInfoCategory.registerRecipes(registration);
        GTRecipeJEICategory.registerRecipes(registration);
        if (!ConfigHolder.INSTANCE.compat.hideOreProcessingDiagrams)
            GTOreProcessingInfoCategory.registerRecipes(registration);
        GTOreVeinInfoCategory.registerRecipes(registration);
        GTBedrockFluidInfoCategory.registerRecipes(registration);
        if (ConfigHolder.INSTANCE.machines.doBedrockOres)
            GTBedrockOreInfoCategory.registerRecipes(registration);
        registration.addRecipes(GTProgrammedCircuitCategory.RECIPE_TYPE,
                List.of(new GTProgrammedCircuitCategory.GTProgrammedCircuitWrapper()));
    }

    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registry) {
        if (LDLib.isReiLoaded() || LDLib.isEmiLoaded()) return;
        GTCEu.LOGGER.info("JEI register ingredients");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(GTItems.PROGRAMMED_CIRCUIT.asItem());
    }
}
