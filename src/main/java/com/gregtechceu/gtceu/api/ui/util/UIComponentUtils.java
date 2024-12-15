package com.gregtechceu.gtceu.api.ui.util;

import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author KilaBash
 * @date 2023/3/29
 * @implNote WidgetUtils
 */
public class UIComponentUtils {

    public static List<UIComponent> getComponentsById(ParentUIComponent group, String regex) {
        return group.childrenByPattern(Pattern.compile(regex));
    }

    @Nullable
    public static Widget getFirstComponentById(WidgetGroup group, String regex) {
        return group.getFirstWidgetById(Pattern.compile(regex));
    }

    public static void componentByIdForEach(ParentUIComponent group, String regex, Consumer<UIComponent> consumer) {
        getComponentsById(group, regex).forEach(consumer);
    }

    public static <T extends UIComponent> void componentByIdForEach(ParentUIComponent group, String regex, Class<T> clazz,
                                                                    Consumer<T> consumer) {
        for (UIComponent widget : getComponentsById(group, regex)) {
            if (clazz.isInstance(widget)) {
                consumer.accept(clazz.cast(widget));
            }
        }
    }

    public static int componentIdIndex(UIComponent widget) {
        String id = widget.id();
        if (id == null) return -1;
        var split = id.split("\\.");
        if (split.length == 0) return -1;
        var end = split[split.length - 1];
        try {
            return Integer.parseInt(end);
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getInventoryHeight(boolean includeHotbar) {
        return 64 + (includeHotbar ? 22 : 0);
    }
}
