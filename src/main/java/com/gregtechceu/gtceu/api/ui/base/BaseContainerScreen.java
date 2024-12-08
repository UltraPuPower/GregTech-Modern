package com.gregtechceu.gtceu.api.ui.base;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.UIContainer;
import com.gregtechceu.gtceu.api.ui.component.SlotComponent;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.inject.GreedyInputUIComponent;
import com.gregtechceu.gtceu.api.ui.util.DisposableScreen;
import com.gregtechceu.gtceu.api.ui.util.UIErrorToast;
import com.gregtechceu.gtceu.api.ui.util.pond.UISlotExtension;

import com.gregtechceu.gtceu.core.mixins.ui.accessor.SlotAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import lombok.Getter;
import org.apache.commons.lang3.mutable.MutableInt;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiFunction;

public abstract class BaseContainerScreen<R extends ParentUIComponent, S extends AbstractContainerMenu>
                                         extends AbstractContainerScreen<S> implements DisposableScreen {

    /**
     * The UI adapter of this screen. This handles
     * all user input as well as setting up GL state for rendering
     * and managing component focus
     */
    @Getter
    @Nullable
    protected UIAdapter<R> uiAdapter = null;

    /**
     * Whether this screen has encountered an unrecoverable
     * error during its lifecycle and should thus close
     * itself on the next frame
     */
    protected boolean invalid = false;

    protected BaseContainerScreen(S handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    /**
     * Initialize the UI adapter for this screen. Usually
     * the body of this method will simply consist of a call
     * to {@link UIAdapter#create(Screen, BiFunction)}
     *
     * @return The UI adapter for this screen to use
     */
    protected abstract @Nullable UIAdapter<R> createAdapter();

    /**
     * Build the component hierarchy of this screen,
     * called after the adapter and root component have been
     * initialized by {@link #createAdapter()}
     *
     * @param rootComponent The root component created in the previous initialization step
     */
    protected abstract void build(R rootComponent);

    @Override
    protected void init() {

        if (this.invalid) return;

        // Check whether this screen was already initialized
        if (this.uiAdapter != null) {
            // If it was, only resize the adapter instead of recreating it - this preserves UI state
            this.uiAdapter.moveAndResize(0, 0, this.width, this.height);
            // Re-add it as a child to circumvent vanilla clearing them
            this.addRenderableWidget(this.uiAdapter);
        } else {
            try {
                this.uiAdapter = this.createAdapter();
                if (this.uiAdapter == null) {
                    this.invalid = true;
                    this.onClose();
                    return;
                }
                MutableInt width = new MutableInt(0);
                MutableInt height = new MutableInt(0);
                this.uiAdapter.rootComponent.forEachDescendant(child -> {
                    if (child.width() > width.getValue()) {
                        width.setValue(child.width());
                    }
                    if (child.height() > height.getValue()) {
                        height.setValue(child.height());
                    }
                });
                this.imageWidth = width.getValue();
                this.imageHeight = height.getValue();
                super.init();

                this.addRenderableWidget(this.uiAdapter);
                this.setFocused(this.uiAdapter);
                this.uiAdapter.container(this.menu);

                this.build(this.uiAdapter.rootComponent);
                this.uiAdapter.rootComponent.setAdapter(this.uiAdapter);

                this.uiAdapter.moveAndResize(0, 0, this.width, this.height);

                this.uiAdapter.leftPos(leftPos);
                this.uiAdapter.topPos(topPos);
                for (UIComponent child : this.uiAdapter.rootComponent.children()) {
                    child.x(child.x() + leftPos);
                    child.y(child.y() + topPos);
                }
                this.menu.slots.forEach(slot -> {
                    ((SlotAccessor) slot).gtceu$setX(slot.x - leftPos);
                    ((SlotAccessor) slot).gtceu$setY(slot.y - topPos);
                });
            } catch (Exception error) {
                GTCEu.LOGGER.warn("Could not initialize gtceu screen", error);
                UIErrorToast.report(error);
                this.invalid = true;
            }
        }
    }

    /**
     * Disable the slot at the given index. Note
     * that this is hard override and the slot cannot
     * re-enable itself
     *
     * @param index The index of the slot to disable
     */
    protected void disableSlot(int index) {
        ((UISlotExtension) this.menu.slots.get(index)).gtceu$setDisabledOverride(true);
    }

    /**
     * Disable the given slot. Note that
     * this is hard override and the slot cannot
     * re-enable itself
     */
    protected void disableSlot(Slot slot) {
        ((UISlotExtension) slot).gtceu$setDisabledOverride(true);
    }

    /**
     * Enable the slot at the given index. Note
     * that this is an override and cannot enable
     * a slot that is disabled through its own will
     *
     * @param index The index of the slot to enable
     */
    protected void enableSlot(int index) {
        ((UISlotExtension) this.menu.slots.get(index)).gtceu$setDisabledOverride(false);
    }

    /**
     * Enable the given slot. Note that
     * this is an override and cannot enable
     * a slot that is disabled through its own will
     */
    protected void enableSlot(Slot slot) {
        ((UISlotExtension) slot).gtceu$setDisabledOverride(true);
    }

    protected boolean isSlotEnabled(int index) {
        return ((UISlotExtension) this.menu.slots.get(index)).gtceu$getDisabledOverride();
    }

    protected boolean isSlotEnabled(Slot slot) {
        return ((UISlotExtension) slot).gtceu$getDisabledOverride();
    }

    /**
     * Wrap the slot at the given index in this screen's
     * handler into a component, so it can be managed by the UI system
     *
     * @param index The index the slot occupies in the handler's slot list
     * @return The wrapped slot
     */
    protected SlotComponent slotAsComponent(int index) {
        if (this.getMenu() instanceof UIContainer container) {
            return container.getSlotMap().get(container.getSlot(index));
        }
        return null;
    }

    /**
     * A convenience shorthand for querying a component from the adapter's
     * root component via {@link ParentUIComponent#childById(Class, String)}
     */
    protected <C extends UIComponent> @Nullable C component(Class<C> expectedClass, String id) {
        return this.uiAdapter.rootComponent.childById(expectedClass, id);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        var context = UIGuiGraphics.of(guiGraphics);
        if (!this.invalid) {
            super.render(context, mouseX, mouseY, delta);

            if (this.uiAdapter.enableInspector) {
                context.pose().translate(0, 0, 500);

                for (int i = 0; i < this.menu.slots.size(); i++) {
                    var slot = this.menu.slots.get(i);
                    if (!slot.hasItem()) continue;

                    context.drawText(Component.literal("H:" + i),
                            this.leftPos + slot.x + 15, this.topPos + slot.y + 9, .5f, 0x0096FF,
                            UIGuiGraphics.TextAnchor.BOTTOM_RIGHT);
                    context.drawText(Component.literal("I:" + slot.getContainerSlot()),
                            this.leftPos + slot.x + 15, this.topPos + slot.y + 15, .5f, 0x5800FF,
                            UIGuiGraphics.TextAnchor.BOTTOM_RIGHT);
                }

                context.pose().translate(0, 0, -500);
            }

            this.renderTooltip(context, mouseX, mouseY);
        } else {
            this.onClose();
        }
    }

    // stop the MC labels from rendering entirely.
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }

        return (modifiers & GLFW.GLFW_MOD_CONTROL) == 0 &&
                this.uiAdapter.rootComponent.focusHandler().focused() instanceof GreedyInputUIComponent inputComponent ?
                        inputComponent.onKeyPress(keyCode, scanCode, modifiers) :
                        super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.uiAdapter.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) ||
                super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.uiAdapter;
    }

    @Override
    public void removed() {
        super.removed();
        if (this.uiAdapter != null) {
            this.uiAdapter.cursorAdapter.applyStyle(CursorStyle.NONE);
        }
    }

    @Override
    public void dispose() {
        if (this.uiAdapter != null) this.uiAdapter.dispose();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {}
}
