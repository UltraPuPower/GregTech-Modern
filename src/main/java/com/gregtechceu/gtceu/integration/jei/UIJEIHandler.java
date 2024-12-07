package com.gregtechceu.gtceu.integration.jei;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Surface;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import net.minecraft.client.renderer.Rect2i;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIJEIHandler implements IGuiContainerHandler<BaseContainerScreen<?, ?>> {

    @Override
    public List<Rect2i> getGuiExtraAreas(BaseContainerScreen<?, ?> screen) {
        if (screen.children().isEmpty()) {
            return Collections.emptyList();
        }

        var adapter = screen.getUiAdapter();
        if (adapter == null) return Collections.emptyList();

        var rootComponent = adapter.rootComponent;
        var children = new ArrayList<UIComponent>();
        rootComponent.collectDescendants(children);
        children.remove(rootComponent);

        List<Rect2i> areas = new ArrayList<>();
        children.forEach(component -> {
            if (component instanceof ParentUIComponent parent && parent.surface() == Surface.BLANK) return;

            var size = component.fullSize();
            areas.add(new Rect2i(component.x(), component.y(), size.width(), size.height()));
        });
        return areas;
    }
}
