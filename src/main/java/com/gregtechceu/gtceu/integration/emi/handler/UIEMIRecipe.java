package com.gregtechceu.gtceu.integration.emi.handler;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.ui.component.SlotComponent;
import com.gregtechceu.gtceu.api.ui.component.TankComponent;
import com.gregtechceu.gtceu.api.ui.core.ParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Size;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.ingredient.ClickableIngredientSlot;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class UIEMIRecipe<T extends UIComponent> implements EmiRecipe {

    protected T component;
    protected EMIUIAdapter adapter;
    @Getter
    protected List<EmiIngredient> inputs = new ArrayList<>();
    @Getter
    protected List<EmiStack> outputs = new ArrayList<>();
    @Getter
    protected List<EmiIngredient> catalysts = new ArrayList<>();
    @Getter
    protected int displayWidth, displayHeight;

    /**
     * Create a new {@code UIEMIRecipe}.
     * The given component MUST have a constant size.
     *
     * @param componentSupplier the supplier to create the UI from.
     */
    public UIEMIRecipe(Supplier<T> componentSupplier) {
        this.component = componentSupplier.get();
        // inflate up to a sane default
        this.component.inflate(Size.of(200, 200));

        Bounds bounds = new Bounds(0, 0, this.component.width(), this.component.height());
        this.adapter = new EMIUIAdapter(bounds);
        this.adapter.rootComponent().child(this.component);
        this.adapter.prepare();

        this.displayWidth = this.component.width();
        this.displayHeight = this.component.height();

        for (UIComponent c : getFlatWidgetCollection(this.component)) {
            if (c instanceof ClickableIngredientSlot<?> slot) {
                var io = slot.ingredientIO();

                EmiIngredient ingredient;
                var override = slot.ingredientOverride();
                if (override != null) {
                    ingredient = (EmiIngredient) override;
                } else {
                    var converter = EmiStackConverter.getForNullable(slot.ingredientClass());
                    if (converter == null) {
                        continue;
                    }
                    // noinspection unchecked,rawtypes
                    ingredient = ((EmiStackConverter.Converter) converter).convertTo(slot);
                }

                if (io == IO.IN || io == IO.BOTH) {
                    inputs.add(ingredient);
                }
                if (io == IO.OUT || io == IO.BOTH) {
                    outputs.add(ingredient.getEmiStacks().get(0));
                }
                if (io == IO.NONE) {
                    catalysts.add(ingredient);
                }
            }
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.add(this.adapter);

        for (UIComponent w : getFlatWidgetCollection(component)) {
            if (w instanceof ClickableIngredientSlot<?> slot) {
                /*
                 * do we still want this?
                 * if (w.parent() instanceof DraggableScrollableWidgetGroup draggable && draggable.isUseScissor()) {
                 * // don't add the EMI widget at all if we have a draggable group, let the draggable widget handle it
                 * instead.
                 * continue;
                 * }
                 */
                var io = slot.ingredientIO();
                if (io != null) {
                    EmiIngredient ingredient;

                    var override = slot.ingredientOverride();
                    if (override != null) {
                        ingredient = (EmiIngredient) override;
                    } else {
                        var converter = EmiStackConverter.getForNullable(slot.ingredientClass());
                        if (converter == null) {
                            continue;
                        }
                        // noinspection unchecked,rawtypes
                        ingredient = ((EmiStackConverter.Converter) converter).convertTo(slot);
                    }

                    SlotWidget slotWidget = null;
                    // Clear the LDLib slots & add EMI slots based on them.
                    if (slot instanceof SlotComponent slotW) {
                        slotW.setSlot((IItemHandlerModifiable) EmptyHandler.INSTANCE, 0)
                                .drawContents(false)
                                .drawTooltip(false);
                    } else if (slot instanceof TankComponent tankW) {
                        tankW.setFluidTank(EmptyFluidHandler.INSTANCE)
                                .drawContents(false)
                                .drawTooltip(false);
                        long capacity = Math.max(1, ingredient.getAmount());
                        slotWidget = new TankWidget(ingredient, w.x(), w.y(), w.width(), w.height(), capacity);
                    }
                    if (slotWidget == null) {
                        slotWidget = new SlotWidget(ingredient, w.x(), w.y());
                    }

                    slotWidget.customBackground(null, w.x(), w.y(), w.width(), w.height()).drawBack(false);
                    if (io == IO.NONE) {
                        slotWidget.catalyst(true);
                    } else if (io == IO.OUT) {
                        slotWidget.recipeContext(this);
                    }
                    var tooltip = w.tooltip();
                    if (tooltip != null) {
                        for (ClientTooltipComponent component : tooltip) {
                            slotWidget.appendTooltip(() -> component);
                        }
                    }
                    widgets.add(slotWidget);
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
}
