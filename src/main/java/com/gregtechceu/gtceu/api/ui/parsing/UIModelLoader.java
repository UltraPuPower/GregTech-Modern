package com.gregtechceu.gtceu.api.ui.parsing;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

public class UIModelLoader implements ResourceManagerReloadListener {

    private static final Map<ResourceLocation, UIModel> LOADED_MODELS = new HashMap<>();

    private static final Map<ResourceLocation, Path> HOT_RELOAD_LOCATIONS = new HashMap<>();

    /**
     * Get the most up-to-date version of the UI model specified
     * by the given identifier. If debug mod is enabled and a hot reload
     * location has been configured by the user for this specific model,
     * a hot reload will be attempted
     *
     * @return The most up-to-date version of the requested model, or
     *         the result of {@link #getPreloaded(ResourceLocation)} if the hot reload
     *         fails for any reason
     */
    public static @Nullable UIModel get(ResourceLocation id) {
        if (ConfigHolder.INSTANCE.dev.debug && HOT_RELOAD_LOCATIONS.containsKey(id)) {
            try (var stream = Files.newInputStream(HOT_RELOAD_LOCATIONS.get(id))) {
                return UIModel.load(stream);
            } catch (ParserConfigurationException | IOException | SAXException e) {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.sendSystemMessage(
                            Component.literal("GTM hot ui model reload failed, check the log for details"));
                }
                GTCEu.LOGGER.error("Hot UI model reload failed", e);
            }
        }

        return getPreloaded(id);
    }

    /**
     * Fetch the UI model specified by the given identifier from the
     * cache created during the last resource reload
     */
    public static @Nullable UIModel getPreloaded(ResourceLocation id) {
        return LOADED_MODELS.getOrDefault(id, null);
    }

    /**
     * Set the path from which to attempt a hot reload when the UI
     * model with the given identifier is requested through {@link #get(ResourceLocation)}.
     * <p>
     * Call with a {@code null} path to clear
     */
    public static void setHotReloadPath(ResourceLocation modelId, @Nullable Path reloadPath) {
        if (reloadPath != null) {
            HOT_RELOAD_LOCATIONS.put(modelId, reloadPath);
        } else {
            HOT_RELOAD_LOCATIONS.remove(modelId);
        }

        List<String> hotReloadLocs = new ArrayList<>();
        for (var entry : HOT_RELOAD_LOCATIONS.entrySet()) {
            hotReloadLocs.add(entry.getKey().toString() + ';' + entry.getValue().toString());
        }
        ConfigHolder.INSTANCE.dev.guiHotReloadPaths = hotReloadLocs.toArray(String[]::new);
    }

    public static @Nullable Path getHotReloadPath(ResourceLocation modelId) {
        return HOT_RELOAD_LOCATIONS.get(modelId);
    }

    public static Set<ResourceLocation> allLoadedModels() {
        return Collections.unmodifiableSet(LOADED_MODELS.keySet());
    }

    private static final String PATH_PREFIX = "gtceu/ui", PATH_SUFFIX = ".xml";

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        LOADED_MODELS.clear();
        GTRegistries.RECIPE_TYPES.forEach(type -> type.getRecipeUI().reloadCustomUI());
        GTRegistries.MACHINES.forEach(def -> {
            if (def.getEditableUI() != null) {
                def.getEditableUI().reloadCustomUI();
            }
        });

        manager.listResources(PATH_PREFIX, identifier -> identifier.getPath().endsWith(PATH_SUFFIX))
                .forEach((resourceId, resource) -> {
                    try {
                        //+1 for the / at the end.
                        var modelId = resourceId.withPath(path -> path.substring(PATH_PREFIX.length() + 1,
                                path.length() - PATH_SUFFIX.length()));

                        LOADED_MODELS.put(modelId, UIModel.load(resource.open()));
                    } catch (ParserConfigurationException | IOException | SAXException e) {
                        GTCEu.LOGGER.error("Could not parse UI model {}", resourceId, e);
                    }
                });
    }

    static {
        if (ConfigHolder.INSTANCE.dev.debug) {
            var associations = ConfigHolder.INSTANCE.dev.guiHotReloadPaths;
            for (String assoc : associations) {
                String[] split = assoc.split(";");
                HOT_RELOAD_LOCATIONS.put(new ResourceLocation(split[0]),
                        Path.of(Minecraft.getInstance().gameDirectory.getPath(), split[1]));
            }
        }
    }
}
