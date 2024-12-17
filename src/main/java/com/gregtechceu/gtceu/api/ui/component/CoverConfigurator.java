package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.IUICover;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.common.data.GTItems;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class CoverConfigurator implements IFancyConfigurator {

    protected final ICoverable coverable;
    // runtime
    @Nullable
    protected final Direction side;
    @Nullable
    protected final CoverBehavior coverBehavior;

    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.gui.cover_setting.title");
    }

    @Override
    public UITexture getIcon() {
        return UITextures.item(GTItems.ITEM_FILTER.asStack());
    }

    @Override
    public ParentUIComponent createConfigurator(UIAdapter<StackLayout> adapter) {
        StackLayout group = UIContainers.stack(Sizing.content(), Sizing.content());
        if (side != null) {
            if (coverable.getCoverAtSide(side) instanceof IUICover iuiCover) {
                ParentUIComponent coverConfigurator = iuiCover.createUIWidget(adapter);
                coverConfigurator.surface(Surface.UI_BACKGROUND);
                coverConfigurator.positioning(Positioning.absolute(4, -4));
                group.child(coverConfigurator);
                group.sizing(Sizing.fixed(Math.max(120, coverConfigurator.width() + 8)),
                        Sizing.fixed(Math.max(80, 80 + coverConfigurator.height())));
            }
        }
        return group;
    }
}
