package com.gregtechceu.gtceu.integration.rei.handler;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Size;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.ingredient.ClickableIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import lombok.Getter;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UIREIDisplay<T extends UIComponent> implements Display {

    protected T component;
    protected REIUIAdapter adapter;
    @Getter
    protected List<EntryIngredient> inputEntries;
    @Getter
    protected List<EntryIngredient> outputEntries;
    protected List<EntryIngredient> catalysts;
    @Getter
    protected final CategoryIdentifier<?> categoryIdentifier;

    public UIREIDisplay(Supplier<T> componentSupplier, CategoryIdentifier<?> category) {
        this.component = componentSupplier.get();
        // inflate up to a sane default
        this.component.inflate(Size.of(200, 200));

        Rectangle bounds = new Rectangle(0, 0, this.component.width(), this.component.height());
        this.adapter = new REIUIAdapter(bounds);
        adapter.rootComponent().child(this.component);
        adapter.prepare();

        this.inputEntries = new ArrayList<>();
        this.outputEntries = new ArrayList<>();
        this.catalysts = new ArrayList<>();
        this.categoryIdentifier = category;

        for (UIComponent c : getFlatWidgetCollection(this.component)) {
            if (c instanceof ClickableIngredientSlot<?> slot) {
                var io = slot.ingredientIO();

                EntryIngredient ingredient = getIngredient(slot);
                if (io == IO.IN || io == IO.BOTH) {
                    inputEntries.add(ingredient);
                }
                if (io == IO.OUT || io == IO.BOTH) {
                    outputEntries.add(ingredient);
                }
                if (io == IO.NONE) {
                    catalysts.add(ingredient);
                }
            }
        }
    }

    public List<UIComponent> getFlatWidgetCollection(T widgetIn) {
        List<UIComponent> widgetList = new ArrayList<>();
        if (widgetIn instanceof ParentUIComponent group) {
            group.collectDescendants(widgetList);
        } else {
            widgetList.add(widgetIn);
        }
        return widgetList;
    }

    @Nullable
    public EntryIngredient getIngredient(ClickableIngredientSlot<?> slot) {
        EntryIngredient ingredient;
        var override = slot.ingredientOverride();
        if (override != null) {
            ingredient = (EntryIngredient) override;
        } else {
            var converter = REIStackConverter.getForNullable(slot.ingredientClass());
            if (converter == null) {
                return null;
            }
            //noinspection unchecked,rawtypes
            ingredient = ((REIStackConverter.Converter) converter).convertTo(slot);
        }
        return ingredient;
    }

    public List<Widget> createWidget(Rectangle bounds) {
        List<Widget> list = new ArrayList<>();
        var widget = this.component;

        list.add(Widgets.createRecipeBase(bounds));
        list.add(adapter);

        for (UIComponent w : getFlatWidgetCollection(widget)) {
            if (w instanceof ClickableIngredientSlot<?> slot) {
                /*
                if (w.getParent() instanceof DraggableScrollableWidgetGroup draggable && draggable.isUseScissor()) {
                    // don't add the REI widget at all if we have a draggable group, let the draggable widget handle it instead.
                    continue;
                }
                */
                EntryWidget entryWidget = new EntryWidget(new Rectangle(slot.x(), slot.y(),
                        slot.width(), slot.height()))
                        .noBackground();

                if (slot.ingredientIO() == IO.IN) {
                    entryWidget.markIsInput();
                } else if (slot.ingredientIO() == IO.OUT) {
                    entryWidget.markIsOutput();
                } else {
                    entryWidget.unmarkInputOrOutput();
                }
                list.add(entryWidget);
                entryWidget.entries(getIngredient(slot));

                // Clear the LDLib slots
                if (slot instanceof SlotWidget slotW) {
                    slotW.setHandlerSlot(IItemTransfer.EMPTY, 0);
                    slotW.setDrawHoverOverlay(false);
                } else if (slot instanceof TankWidget tankW) {
                    tankW.setFluidTank(IFluidStorage.EMPTY);
                    tankW.setDrawHoverOverlay(false);
                }
                entryWidget.tooltipProcessor(tooltips -> {
                    // remove all REI tooltips, let the UI component draw them itself.
                    tooltips.entries().clear();
                    return tooltips;
                });
            }
        }

        return list;
    }

    @Override
    public List<EntryIngredient> getRequiredEntries() {
        var required = new ArrayList<>(catalysts);
        required.addAll(inputEntries);
        return required;
    }

}
