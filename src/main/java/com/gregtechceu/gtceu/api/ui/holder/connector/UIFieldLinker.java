package com.gregtechceu.gtceu.api.ui.holder.connector;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.holder.connector.annotation.UILinkSetter;
import com.gregtechceu.gtceu.api.ui.holder.connector.annotation.UIFieldLink;
import com.gregtechceu.gtceu.api.ui.util.UIErrorToast;
import com.lowdragmc.lowdraglib.Platform;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

public class UIFieldLinker {

    public static <R extends ParentUIComponent> void findAndLinkFields(final UIAdapter<R> adapter, Object from) {
        final Map<String, Object> toLink = new Object2ObjectOpenHashMap<>();
        for (Field field : from.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            UIFieldLink elementLink = field.getAnnotation(UIFieldLink.class);
            if (elementLink != null) {
                try {
                    toLink.put(elementLink.value(), field.get(from));
                } catch (IllegalAccessException e) {
                    GTCEu.LOGGER.warn("Could not get field {} in class {} for UI attachment",
                            field.getName(), from.getClass().getSimpleName(), e);
                    if (Platform.isClient()) {
                        ClientHandler.closeCurrentScreen(e);
                    }
                }
            }
        }

        adapter.rootComponent.forEachDescendant(child -> {
            if (child.id() != null) {
                attachComponentLinks(child, toLink);
            }
        });
    }

    private static void attachComponentLinks(UIComponent component, final Map<String, Object> toLink) {
        for (Method method : component.getClass().getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                return;
            }
            final UILinkSetter elementLink = method.getAnnotation(UILinkSetter.class);
            if (elementLink == null) {
                continue;
            }

            var validLink = toLink.entrySet().stream()
                    .filter(link -> link.getKey().startsWith(component.id()) &&
                            elementLink.value().isInstance(link.getValue()))
                    .findFirst()
                    .orElse(null);
            if (validLink != null) {
                try {
                    method.invoke(component, validLink.getValue());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    GTCEu.LOGGER.warn("Could not call method {} in class {} for UI field attachment",
                            method.getName(), component.getClass().getSimpleName(), e);
                    if (Platform.isClient()) {
                        ClientHandler.closeCurrentScreen(e);
                    }
                }
            }
        }
    }

    private static final class ClientHandler {

        private static void closeCurrentScreen(Throwable throwable) {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null) {
                return;
            }
            UIErrorToast.report(throwable);
            Screen screen = mc.screen;
            if (screen != null) {
                screen.onClose();
            }
        }

    }

}
