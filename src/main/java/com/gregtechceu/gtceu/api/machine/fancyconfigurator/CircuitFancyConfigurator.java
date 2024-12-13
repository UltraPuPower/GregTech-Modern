package com.gregtechceu.gtceu.api.machine.fancyconfigurator;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.ButtonComponent;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.GridLayout;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.api.ui.fancy.IFancyCustomMiddleClickAction;
import com.gregtechceu.gtceu.api.ui.fancy.IFancyCustomMouseWheelAction;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.lang.LangHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2023/6/30
 * @implNote CircuitFancyConfigurator
 */
public class CircuitFancyConfigurator implements IFancyConfigurator, IFancyCustomMouseWheelAction,
        IFancyCustomMiddleClickAction {

    private static final int SET_TO_ZERO = 2;
    private static final int SET_TO_EMPTY = 3;
    private static final int SET_TO_N = 4;

    private static final int NO_CONFIG = -1;

    final ItemStackHandler circuitSlot;

    public CircuitFancyConfigurator(ItemStackHandler circuitSlot) {
        this.circuitSlot = circuitSlot;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.gui.circuit.title");
    }

    @Override
    public UITexture getIcon() {
        if (IntCircuitBehaviour.isIntegratedCircuit(circuitSlot.getStackInSlot(0))) {
            return UITextures.item(circuitSlot.getStackInSlot(0));
        }
        return UITextures.group(UITextures.item(IntCircuitBehaviour.stack(0)),
                UITextures.item(Items.BARRIER.getDefaultInstance()));
    }

    @Override
    public boolean mouseWheelMove(BiConsumer<Integer, Consumer<FriendlyByteBuf>> writeClientAction, double mouseX,
                                  double mouseY, double wheelDelta) {
        if (wheelDelta == 0) return false;
        if (!ConfigHolder.INSTANCE.machines.ghostCircuit && circuitSlot.getStackInSlot(0).isEmpty()) return false;
        int nextValue = getNextValue(wheelDelta > 0);
        if (nextValue == NO_CONFIG) {
            if (ConfigHolder.INSTANCE.machines.ghostCircuit) {
                circuitSlot.setStackInSlot(0, ItemStack.EMPTY);
                writeClientAction.accept(SET_TO_EMPTY, buf -> {
                });
            }
        } else {
            circuitSlot.setStackInSlot(0, IntCircuitBehaviour.stack(nextValue));
            writeClientAction.accept(SET_TO_N, buf -> buf.writeVarInt(nextValue));
        }
        return true;
    }

    @Override
    public void receiveMessage(int id, FriendlyByteBuf buffer) {
        switch (id) {
            case SET_TO_ZERO -> {
                if (ConfigHolder.INSTANCE.machines.ghostCircuit || !circuitSlot.getStackInSlot(0).isEmpty())
                    circuitSlot.setStackInSlot(0, IntCircuitBehaviour.stack(0));
            }
            case SET_TO_EMPTY -> {
                if (ConfigHolder.INSTANCE.machines.ghostCircuit || circuitSlot.getStackInSlot(0).isEmpty())
                    circuitSlot.setStackInSlot(0, ItemStack.EMPTY);
                else
                    circuitSlot.setStackInSlot(0, IntCircuitBehaviour.stack(0));
            }
            case SET_TO_N -> {
                if (ConfigHolder.INSTANCE.machines.ghostCircuit || !circuitSlot.getStackInSlot(0).isEmpty())
                    circuitSlot.setStackInSlot(0, IntCircuitBehaviour.stack(buffer.readVarInt()));
            }
        }
    }

    @Override
    public void onMiddleClick(BiConsumer<Integer, Consumer<FriendlyByteBuf>> writeClientAction) {
        if (!ConfigHolder.INSTANCE.machines.ghostCircuit && !circuitSlot.getStackInSlot(0).isEmpty()) {
            circuitSlot.setStackInSlot(0, IntCircuitBehaviour.stack(0));
        } else {
            circuitSlot.setStackInSlot(0, ItemStack.EMPTY);
            writeClientAction.accept(SET_TO_EMPTY, buf -> {
            });
        }
    }

    @Override
    public UIComponent createConfigurator(UIAdapter<UIComponentGroup> adapter) {
        var group = UIContainers.group(Sizing.fixed(174), Sizing.fixed(132));
        // FIXME MAKE TRANSLATABLE
        group.child(UIComponents.label(Component.literal("Programmed Circuit Configuration"))
                .positioning(Positioning.absolute(9, 8)));
        group.child(UIComponents.slot(circuitSlot, 0)
                .backgroundTexture(UITextures.group(GuiTextures.SLOT, GuiTextures.INT_CIRCUIT_OVERLAY))
                .canExtract(!ConfigHolder.INSTANCE.machines.ghostCircuit)
                .canInsert(!ConfigHolder.INSTANCE.machines.ghostCircuit)
                .positioning(Positioning.relative(50, 15)));
        if (ConfigHolder.INSTANCE.machines.ghostCircuit) {
            group.child(UIComponents.button(Component.empty(),
                            clickData -> {
                                if (!clickData.isRemote) {
                                    circuitSlot.setStackInSlot(0, ItemStack.EMPTY);
                                }
                            })
                    .renderer(ButtonComponent.Renderer.EMPTY)
                    .positioning(Positioning.relative(50, 15))
                    .sizing(Sizing.fixed(18)));
        }

        GridLayout grid = UIContainers.grid(Sizing.content(), Sizing.content(), 9, 2);
        int idx = 0;
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 8; y++) {
                int finalIdx = idx;
                grid.child(UIComponents.button(Component.empty(),
                        clickData -> {
                            if (!clickData.isRemote) {
                                ItemStack stack = circuitSlot.getStackInSlot(0).copy();
                                if (IntCircuitBehaviour.isIntegratedCircuit(stack)) {
                                    IntCircuitBehaviour.setCircuitConfiguration(stack, finalIdx);
                                    circuitSlot.setStackInSlot(0, stack);
                                } else if (ConfigHolder.INSTANCE.machines.ghostCircuit) {
                                    circuitSlot.setStackInSlot(0, IntCircuitBehaviour.stack(finalIdx));
                                }
                            }
                        }).renderer(ButtonComponent.Renderer.texture(UITextures.group(GuiTextures.SLOT,
                                UITextures.item(IntCircuitBehaviour.stack(finalIdx)).scale(16f / 18))))
                        .positioning(Positioning.absolute(5 + (18 * y), 48 + (18 * x)))
                        .sizing(Sizing.fixed(18)), y, x);
                idx++;
            }
        }
        for (int x = 0; x <= 5; x++) {
            int finalIdx = x + 27;
            grid.child(UIComponents.button(Component.empty(),
                    clickData -> {
                        if (!clickData.isRemote) {
                            ItemStack stack = circuitSlot.getStackInSlot(0).copy();
                            if (IntCircuitBehaviour.isIntegratedCircuit(stack)) {
                                IntCircuitBehaviour.setCircuitConfiguration(stack, finalIdx);
                                circuitSlot.setStackInSlot(0, stack);
                            } else if (ConfigHolder.INSTANCE.machines.ghostCircuit) {
                                circuitSlot.setStackInSlot(0, IntCircuitBehaviour.stack(finalIdx));
                            }
                        }
                    }).renderer(ButtonComponent.Renderer.texture(UITextures.group(GuiTextures.SLOT,
                            UITextures.item(IntCircuitBehaviour.stack(finalIdx)).scale(16f / 18))))
                    .positioning(Positioning.absolute(5 + (18 * x), 102))
                    .sizing(Sizing.fixed(18)), 8, x);
        }
        group.child(grid);
        return group;
    }

    @Override
    public List<Component> getTooltips() {
        var list = new ArrayList<>(IFancyConfigurator.super.getTooltips());
        list.addAll(LangHandler.getMultiLang("gtceu.gui.configurator_slot.tooltip"));
        return list;
    }

    private int getNextValue(boolean increment) {
        int currentValue = IntCircuitBehaviour.getCircuitConfiguration(circuitSlot.getStackInSlot(0));
        if (increment) {
            // if at max, loop around to no circuit
            if (currentValue == IntCircuitBehaviour.CIRCUIT_MAX) {
                return 0;
            }
            // if at no circuit, skip 0 and return 1
            if (this.circuitSlot.getStackInSlot(0).isEmpty()) {
                return 1;
            }
            // normal case: increment by 1
            return currentValue + 1;
        } else {
            // if at no circuit, loop around to max
            if (this.circuitSlot.getStackInSlot(0).isEmpty() ||
                    (currentValue == 0 && !ConfigHolder.INSTANCE.machines.ghostCircuit)) {
                return IntCircuitBehaviour.CIRCUIT_MAX;
            }
            // if at 1, skip 0 and return no circuit
            if (currentValue == 1 && ConfigHolder.INSTANCE.machines.ghostCircuit) {
                return -1;
            }
            // normal case: decrement by 1
            return currentValue - 1;
        }
    }

}
