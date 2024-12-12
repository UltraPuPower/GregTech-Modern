package com.gregtechceu.gtceu.integration.jei;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Surface;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import me.shedaniel.math.Rectangle;
import net.minecraft.client.renderer.Rect2i;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIJEIHandler implements IGuiContainerHandler<BaseContainerScreen<?, ?>> {

    @Override
    public List<Rect2i> getGuiExtraAreas(BaseContainerScreen<?, ?> screen) {
        return screen.componentsForExclusionAreas()
                .map(rect -> new Rect2i(rect.x(), rect.y(), rect.width(), rect.height()))
                .toList();
    }
}
