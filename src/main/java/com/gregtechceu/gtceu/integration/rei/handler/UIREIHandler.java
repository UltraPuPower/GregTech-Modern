package com.gregtechceu.gtceu.integration.rei.handler;

import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.ingredient.GhostIngredientSlot;

import net.minecraft.client.gui.screens.Screen;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.*;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class UIREIHandler implements DraggableStackVisitor<BaseContainerScreen<?, ?>>,
                          ExclusionZonesProvider<BaseContainerScreen<?, ?>> {

    public static final UIREIHandler INSTANCE = new UIREIHandler();

    private UIREIHandler() {}

    @Override
    public <R extends Screen> boolean isHandingScreen(R screen) {
        return screen instanceof BaseContainerScreen<?, ?>;
    }

    @Override
    public Stream<BoundsProvider> getDraggableAcceptingBounds(DraggingContext<BaseContainerScreen<?, ?>> context,
                                                              DraggableStack stack) {
        return context.getScreen()
                .componentsForGhostIngredients()
                .map(target -> BoundsProvider.ofRectangle(
                        new Rectangle(target.x(), target.y(), target.width(), target.height())));
    }

    @Override
    public DraggedAcceptorResult acceptDraggedStack(DraggingContext<BaseContainerScreen<?, ?>> context,
                                                    DraggableStack stack) {
        List<GhostIngredientSlot<?>> ghostSlots = context.getScreen().componentsForGhostIngredients().toList();
        for (var slot : ghostSlots) {
            if (slot.containerAccess() == null || !slot.enabled()) {
                continue;
            }
            var entryStack = stack.getStack();

            if (slot.ingredientHandlingOverride(entryStack)) {
                return DraggedAcceptorResult.ACCEPTED;
            }
            REIStackConverter.Converter<?> converter = REIStackConverter.getForNullable(slot.ghostIngredientClass());
            if (converter == null) {
                continue;
            }
            var converted = converter.convertFrom(entryStack);
            if (converted != null) {
                // noinspection unchecked,rawtypes
                ((GhostIngredientSlot) slot).setGhostIngredient(converted);
                return DraggedAcceptorResult.ACCEPTED;
            }
        }
        return DraggedAcceptorResult.PASS;
    }

    @Override
    public Collection<Rectangle> provide(BaseContainerScreen<?, ?> screen) {
        return screen.componentsForExclusionAreas()
                .map(rect -> new Rectangle(rect.x(), rect.y(), rect.width(), rect.height()))
                .toList();
    }
}
