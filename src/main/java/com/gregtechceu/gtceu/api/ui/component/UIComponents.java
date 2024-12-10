package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UIComponents {

    private UIComponents() {}

    // -----------------------
    // Wrapped Vanilla Widgets
    // -----------------------

    public static ButtonComponent button(Component message, Consumer<ButtonComponent> onPress) {
        return new ButtonComponent(message, onPress);
    }

    public static TextBoxComponent textBox(Sizing horizontalSizing) {
        return new TextBoxComponent(horizontalSizing);
    }

    public static TextBoxComponent textBox(Sizing horizontalSizing, String text) {
        var textBox = new TextBoxComponent(horizontalSizing);
        textBox.text(text);
        return textBox;
    }

    public static TextAreaComponent textArea(Sizing horizontalSizing, Sizing verticalSizing) {
        return new TextAreaComponent(horizontalSizing, verticalSizing);
    }

    public static TextAreaComponent textArea(Sizing horizontalSizing, Sizing verticalSizing, String text) {
        var textArea = new TextAreaComponent(horizontalSizing, verticalSizing);
        textArea.setValue(text);
        return textArea;
    }

    // ------------------
    // Default Components
    // ------------------

    public static SlotComponent slot(IItemHandlerModifiable handler, int index) {
        return new SlotComponent(handler, index);
    }

    public static SlotComponent slot(Container handler, int index) {
        return new SlotComponent(handler, index);
    }

    public static SlotComponent slot(Slot slot) {
        return new SlotComponent(slot);
    }

    public static PlayerInventoryComponent playerInventory(Inventory inventory) {
        return new PlayerInventoryComponent(inventory);
    }

    public static PlayerInventoryComponent playerInventory(AbstractContainerMenu menu, int startSlotIndex) {
        return new PlayerInventoryComponent(menu, startSlotIndex);
    }

    public static <E extends Entity> EntityComponent<E> entity(Sizing sizing, EntityType<E> type,
                                                               @Nullable CompoundTag nbt) {
        return new EntityComponent<>(sizing, type, nbt);
    }

    public static <E extends Entity> EntityComponent<E> entity(Sizing sizing, E entity) {
        return new EntityComponent<>(sizing, entity);
    }

    public static ItemComponent item(ItemStack item) {
        return new ItemComponent(item);
    }

    public static BlockComponent block(BlockState state) {
        return new BlockComponent(state, null);
    }

    public static BlockComponent block(BlockState state, BlockEntity blockEntity) {
        return new BlockComponent(state, blockEntity);
    }

    public static BlockComponent block(BlockState state, @Nullable CompoundTag nbt) {
        final var client = Minecraft.getInstance();

        BlockEntity blockEntity = null;

        if (state.getBlock() instanceof EntityBlock provider) {
            blockEntity = provider.newBlockEntity(client.player.blockPosition(), state);
            BlockComponent.prepareBlockEntity(state, blockEntity, nbt);
        }

        return new BlockComponent(state, blockEntity);
    }

    public static FluidComponent fluid(FluidStack fluid) {
        return new FluidComponent(fluid);
    }

    public static TankComponent tank(IFluidHandler fluidHandler) {
        return new TankComponent(fluidHandler, 0);
    }

    public static TankComponent tank(IFluidHandler fluidHandler, int tank) {
        return new TankComponent(fluidHandler, tank);
    }

    public static LabelComponent label(Component text) {
        return new LabelComponent(text);
    }

    public static CheckboxComponent checkbox(Component message) {
        return new CheckboxComponent(message);
    }

    public static SliderComponent slider(Sizing horizontalSizing) {
        return new SliderComponent(horizontalSizing);
    }

    public static DiscreteSliderComponent discreteSlider(Sizing horizontalSizing, double min, double max) {
        return new DiscreteSliderComponent(horizontalSizing, min, max);
    }

    public static SpriteComponent sprite(Material spriteId) {
        return new SpriteComponent(spriteId.sprite());
    }

    public static SpriteComponent sprite(TextureAtlasSprite sprite) {
        return new SpriteComponent(sprite);
    }

    public static NinePatchTextureComponent ninePatchTexture(ResourceLocation texture) {
        return new NinePatchTextureComponent(texture);
    }

    public static TextureComponent texture(ResourceLocation texture, int u, int v, int regionWidth, int regionHeight,
                                           int textureWidth, int textureHeight) {
        return new TextureComponent(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public static TextureComponent texture(ResourceLocation texture, int u, int v, int regionWidth, int regionHeight) {
        return new TextureComponent(texture, u, v, regionWidth, regionHeight, 256, 256);
    }

    public static BoxComponent box(Sizing horizontalSizing, Sizing verticalSizing) {
        return new BoxComponent(horizontalSizing, verticalSizing);
    }

    public static DropdownComponent dropdown(Sizing horizontalSizing) {
        return new DropdownComponent(horizontalSizing);
    }

    public static SlimSliderComponent slimSlider(SlimSliderComponent.Axis axis) {
        return new SlimSliderComponent(axis);
    }

    public static SmallCheckboxComponent smallCheckbox(Component label) {
        return new SmallCheckboxComponent(label);
    }

    // -------
    // Utility
    // -------

    public static <T, C extends UIComponent> FlowLayout list(List<T> data, Consumer<FlowLayout> layoutConfigurator,
                                                             Function<T, C> componentMaker, boolean vertical) {
        var layout = vertical ? UIContainers.verticalFlow(Sizing.content(), Sizing.content()) :
                UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        layoutConfigurator.accept(layout);

        for (var value : data) {
            layout.child(componentMaker.apply(value));
        }

        return layout;
    }

    public static VanillaWidgetComponent wrapVanillaWidget(AbstractWidget widget) {
        return new VanillaWidgetComponent(widget);
    }

    public static <T extends UIComponent> T createWithSizing(Supplier<T> componentMaker, Sizing horizontalSizing,
                                                             Sizing verticalSizing) {
        var component = componentMaker.get();
        component.sizing(horizontalSizing, verticalSizing);
        return component;
    }
}
