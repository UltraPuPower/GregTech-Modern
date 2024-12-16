package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.ingredient.ClickableIngredientSlot;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelParsingException;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.texture.ProgressTexture;
import com.gregtechceu.gtceu.client.TooltipsHandler;
import com.gregtechceu.gtceu.common.commands.arguments.FluidParser;
import com.gregtechceu.gtceu.integration.xei.entry.EntryList;
import com.gregtechceu.gtceu.integration.xei.entry.fluid.FluidStackList;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Accessors(fluent = true, chain = true)
public class FluidComponent extends BaseUIComponent implements ClickableIngredientSlot<FluidStack> {

    protected final MultiBufferSource.BufferSource bufferBuilder;
    @Getter
    protected FluidStack stack;
    @Getter
    @Setter
    protected int capacity = 16000;
    @Getter
    @Setter
    protected ProgressTexture.FillDirection fillDirection = ProgressTexture.FillDirection.ALWAYS_FULL;

    protected boolean setTooltipFromStack = false;
    @Setter
    protected boolean showAmount = false;
    @Getter
    @Setter
    protected boolean showOverlay = false;

    protected FluidComponent(FluidStack stack) {
        this.bufferBuilder = Minecraft.getInstance().renderBuffers().bufferSource();
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
        var pose = graphics.pose();

        if (stack != null) {
            RenderSystem.disableBlend();
            if (!stack.isEmpty()) {
                double progress = stack.getAmount() * 1.0 /
                        Math.max(Math.max(stack.getAmount(), capacity), 1);
                float drawnU = (float) fillDirection.getDrawnU(progress);
                float drawnV = (float) fillDirection.getDrawnV(progress);
                float drawnWidth = (float) fillDirection.getDrawnWidth(progress);
                float drawnHeight = (float) fillDirection.getDrawnHeight(progress);

                int width = width() - 2;
                int height = height() - 2;
                int x = x() + 1;
                int y = y() + 1;
                graphics.drawFluid(stack, capacity,
                        (int) (x + drawnU * width), (int) (y + drawnV * height),
                        ((int) (width * drawnWidth)), ((int) (height * drawnHeight)));
            }

        }

        if (showAmount && stack != null) {
            pose.pushPose();
            pose.scale(0.5f, 0.5f, 1.0f);
            String s = FormattingUtil.formatBuckets(stack.getAmount());
            var font = Minecraft.getInstance().font;
            graphics.drawString(font, s,
                    (int) ((x + (16 / 3f)) * 2 - font.width(s) + 21),
                    (int) ((y + (16 / 3f) + 6) * 2), Color.WHITE.argb());
            pose.popPose();
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    protected void updateTooltipForStack() {
        if (!this.setTooltipFromStack) return;

        if (!this.stack.isEmpty()) {
            this.tooltip(tooltipFromFluid(this.stack, Minecraft.getInstance().player, null));
        } else {
            this.tooltip((List<ClientTooltipComponent>) null);
        }
    }

    public FluidComponent setTooltipFromStack(boolean setTooltipFromStack) {
        this.setTooltipFromStack = setTooltipFromStack;
        this.updateTooltipForStack();

        return this;
    }

    public boolean setTooltipFromStack() {
        return setTooltipFromStack;
    }

    public FluidComponent stack(FluidStack stack) {
        this.stack = stack;
        this.updateTooltipForStack();

        return this;
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
    public static List<ClientTooltipComponent> tooltipFromFluid(FluidStack stack, @Nullable LocalPlayer player,
                                                                @Nullable TooltipFlag.Default context) {
        if (context == null) {
            context = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED :
                    TooltipFlag.Default.NORMAL;
        }

        var tooltip = new ArrayList<ClientTooltipComponent>();
        tooltip.add(ClientTooltipComponent.create(stack.getDisplayName().getVisualOrderText()));
        tooltip.add(ClientTooltipComponent.create(
                Component.literal(String.format("%,d mB", stack.getAmount()))
                        .withStyle(ChatFormatting.GRAY)
                        .getVisualOrderText()));
        TooltipsHandler.appendFluidTooltips(stack,
                c -> tooltip.add(ClientTooltipComponent.create(c.getVisualOrderText())),
                context);

        return tooltip;
    }

    @Override
    public @UnknownNullability("Nullability depends on the type of ingredient") EntryList<FluidStack> getIngredients() {
        return FluidStackList.of(this.stack);
    }

    @Override
    public @NotNull Class<FluidStack> ingredientClass() {
        return FluidStack.class;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "capacity", UIParsing::parseUnsignedInt, this::capacity);
        UIParsing.apply(children, "fill-direction", UIParsing.parseEnum(ProgressTexture.FillDirection.class),
                this::fillDirection);
        UIParsing.apply(children, "show-amount", UIParsing::parseBool, this::showAmount);
        UIParsing.apply(children, "show-overlay", UIParsing::parseBool, this::showOverlay);
        UIParsing.apply(children, "set-tooltip-from-stack", UIParsing::parseBool, this::setTooltipFromStack);

        UIParsing.apply(children, "stack", e -> e.getTextContent().strip(), stackString -> {
            try {
                var result = FluidParser.parseForFluid(BuiltInRegistries.FLUID.asLookup(),
                        new StringReader(stackString));

                var stack = new FluidStack(result.fluid().value(), 1);
                stack.setTag(result.nbt());

                this.stack(stack);
            } catch (CommandSyntaxException cse) {
                throw new UIModelParsingException("Invalid item stack", cse);
            }
        });
    }
}
