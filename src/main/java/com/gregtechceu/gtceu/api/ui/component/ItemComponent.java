package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelParsingException;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemComponent extends BaseUIComponent {

    protected static final Matrix4f ITEM_SCALING = new Matrix4f().scaling(16, -16, 16);

    protected final MultiBufferSource.BufferSource entityBuffers;
    protected final ItemRenderer itemRenderer;
    protected ItemStack stack;
    protected boolean showOverlay = false;
    protected boolean setTooltipFromStack = false;

    protected ItemComponent(ItemStack stack) {
        this.entityBuffers = Minecraft.getInstance().renderBuffers().bufferSource();
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.stack = stack;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 16;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 16;
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        final boolean notSideLit = !this.itemRenderer.getModel(this.stack, null, null, 0).usesBlockLight();
        if (notSideLit) {
            Lighting.setupForFlatItems();
        }

        var pose = graphics.pose();
        pose.pushPose();

        // Translate to the root of the component
        pose.translate(this.x, this.y, 100);

        // Scale according to component size and translate to the center
        pose.scale(this.width / 16f, this.height / 16f, 1);
        pose.translate(8.0, 8.0, 0.0);

        // Vanilla scaling and y inversion
        if (notSideLit) {
            pose.scale(16, -16, 16);
        } else {
            pose.mulPoseMatrix(ITEM_SCALING);
        }

        var client = Minecraft.getInstance();

        this.itemRenderer.renderStatic(this.stack, ItemDisplayContext.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pose, entityBuffers, client.level, 0);
        this.entityBuffers.endBatch();

        // Clean up
        pose.popPose();

        if (this.showOverlay) {
            graphics.renderItemDecorations(client.font, this.stack, this.x, this.y);
        }
        if (notSideLit) {
            Lighting.setupFor3DItems();
        }
    }

    protected void updateTooltipForStack() {
        if (!this.setTooltipFromStack) return;

        if (!this.stack.isEmpty()) {
            this.tooltip(tooltipFromItem(this.stack, Minecraft.getInstance().player, null));
        } else {
            this.tooltip((List<ClientTooltipComponent>) null);
        }
    }

    public ItemComponent setTooltipFromStack(boolean setTooltipFromStack) {
        this.setTooltipFromStack = setTooltipFromStack;
        this.updateTooltipForStack();

        return this;
    }

    public boolean setTooltipFromStack() {
        return setTooltipFromStack;
    }

    public ItemComponent stack(ItemStack stack) {
        this.stack = stack;
        this.updateTooltipForStack();

        return this;
    }

    public ItemStack stack() {
        return this.stack;
    }

    public ItemComponent showOverlay(boolean drawOverlay) {
        this.showOverlay = drawOverlay;
        return this;
    }

    public boolean showOverlay() {
        return this.showOverlay;
    }

    /**
     * Obtain the full item stack tooltip, including custom components
     * provided via {@link net.minecraft.world.item.Item#getTooltipImage(ItemStack)}
     *
     * @param stack   The item stack from which to obtain the tooltip
     * @param player  The player to use for context, may be {@code null}
     * @param context The tooltip context - {@code null} to fall back to the default provided by
     *                {@link net.minecraft.client.Options#advancedItemTooltips}
     */
    public static List<ClientTooltipComponent> tooltipFromItem(ItemStack stack, @Nullable LocalPlayer player, @Nullable TooltipFlag.Default context) {
        if (context == null) {
            context = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        }

        var tooltip = new ArrayList<ClientTooltipComponent>();
        stack.getTooltipLines(player, context)
                .stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .forEach(tooltip::add);

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        stack.getTooltipImage().ifPresent(data -> {
            //tooltip.add(1, Objects.requireNonNullElseGet( // TODO event fire here i think
                    //bus.post(new RenderTooltipEvent())
                    //TooltipComponentCallback.EVENT.invoker().getComponent(data),
                    //() -> ClientTooltipComponent.create(data)
            //));
        });

        return tooltip;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "show-overlay", UIParsing::parseBool, this::showOverlay);
        UIParsing.apply(children, "set-tooltip-from-stack", UIParsing::parseBool, this::setTooltipFromStack);

        UIParsing.apply(children, "item", UIParsing::parseResourceLocation, itemId -> {
            GTCEu.LOGGER.warn("Deprecated <item> property populated on item component - migrate to <stack> instead");

            var item = BuiltInRegistries.ITEM.getOptional(itemId).orElseThrow(() -> new UIModelParsingException("Unknown item " + itemId));
            this.stack(item.getDefaultInstance());
        });

        UIParsing.apply(children, "stack", $ -> $.getTextContent().strip(), stackString -> {
            try {
                var result = ItemParser.parseForItem(BuiltInRegistries.ITEM.asLookup(), new StringReader(stackString));

                var stack = new ItemStack(result.item());
                stack.setTag(result.nbt());

                this.stack(stack);
            } catch (CommandSyntaxException cse) {
                throw new UIModelParsingException("Invalid item stack", cse);
            }
        });
    }
}
