package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelParsingException;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.NinePatchTexture;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.AbstractWidgetAccessor;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.ButtonAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.systems.RenderSystem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.function.Consumer;

public class ButtonComponent extends Button {

    public static final ResourceLocation ACTIVE_TEXTURE = new ResourceLocation("owo", "button/active");
    public static final ResourceLocation HOVERED_TEXTURE = new ResourceLocation("owo", "button/hovered");
    public static final ResourceLocation DISABLED_TEXTURE = new ResourceLocation("owo", "button/disabled");

    protected Renderer renderer = Renderer.VANILLA;
    protected boolean textShadow = true;

    protected ButtonComponent(Component message, Consumer<ButtonComponent> onPress) {
        super(0, 0, 0, 0, message, button -> onPress.accept((ButtonComponent) button), Button.DEFAULT_NARRATION);
        this.sizing(Sizing.content());
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderer.draw((UIGuiGraphics) graphics, this, delta);

        var textRenderer = Minecraft.getInstance().font;
        int color = this.active ? 0xffffff : 0xa0a0a0;

        if (this.textShadow) {
            graphics.drawCenteredString(textRenderer, this.getMessage(), this.getX() + this.width / 2,
                    this.getY() + (this.height - 8) / 2, color);
        } else {
            graphics.drawString(textRenderer, this.getMessage(),
                    (int) (this.getX() + this.width / 2f - textRenderer.width(this.getMessage()) / 2f),
                    (int) (this.getY() + (this.height - 8) / 2f), color, false);
        }

        Tooltip tooltip = ((AbstractWidgetAccessor) this).gtceu$getTooltip();
        if (this.isHovered && tooltip != null)
            graphics.renderTooltip(textRenderer, tooltip.toCharSequence(Minecraft.getInstance()),
                    DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
    }

    public ButtonComponent onPress(Consumer<ButtonComponent> onPress) {
        ((ButtonAccessor) this).setOnPress(button -> onPress.accept((ButtonComponent) button));
        return this;
    }

    public ButtonComponent renderer(Renderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public Renderer renderer() {
        return this.renderer;
    }

    public ButtonComponent textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public boolean textShadow() {
        return this.textShadow;
    }

    public ButtonComponent active(boolean active) {
        this.active = active;
        return this;
    }

    public boolean active() {
        return this.active;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "text", UIParsing::parseText, this::setMessage);
        UIParsing.apply(children, "text-shadow", UIParsing::parseBool, this::textShadow);
        UIParsing.apply(children, "renderer", Renderer::parse, this::renderer);
    }

    protected CursorStyle gtceu$preferredCursorStyle() {
        return CursorStyle.HAND;
    }

    @FunctionalInterface
    public interface Renderer {

        Renderer VANILLA = (matrices, button, delta) -> {
            RenderSystem.enableDepthTest();

            var texture = button.active ? button.isHovered ? HOVERED_TEXTURE : ACTIVE_TEXTURE : DISABLED_TEXTURE;
            NinePatchTexture.draw(texture, matrices, button.getX(), button.getY(), button.width, button.height);
        };

        static Renderer flat(int color, int hoveredColor, int disabledColor) {
            return (context, button, delta) -> {
                RenderSystem.enableDepthTest();

                if (button.active) {
                    if (button.isHovered) {
                        context.fill(button.getX(), button.getY(), button.getX() + button.width,
                                button.getY() + button.height, hoveredColor);
                    } else {
                        context.fill(button.getX(), button.getY(), button.getX() + button.width,
                                button.getY() + button.height, color);
                    }
                } else {
                    context.fill(button.getX(), button.getY(), button.getX() + button.width,
                            button.getY() + button.height, disabledColor);
                }
            };
        }

        static Renderer texture(ResourceLocation texture, int u, int v, int textureWidth, int textureHeight) {
            return (context, button, delta) -> {
                int renderV = v;
                if (!button.active) {
                    renderV += button.height * 2;
                } else if (button.isHovered()) {
                    renderV += button.height;
                }

                RenderSystem.enableDepthTest();
                context.blit(texture, button.getX(), button.getY(), u, renderV, button.width, button.height,
                        textureWidth, textureHeight);
            };
        }

        void draw(UIGuiGraphics context, ButtonComponent button, float delta);

        static Renderer parse(Element element) {
            var children = UIParsing.<Element>allChildrenOfType(element, Node.ELEMENT_NODE);
            if (children.size() > 1)
                throw new UIModelParsingException("'renderer' declaration may only contain a single child");

            var rendererElement = children.get(0);
            return switch (rendererElement.getNodeName()) {
                case "vanilla" -> VANILLA;
                case "flat" -> {
                    UIParsing.expectAttributes(rendererElement, "color", "hovered-color", "disabled-color");
                    yield flat(
                            Color.parseAndPack(rendererElement.getAttributeNode("color")),
                            Color.parseAndPack(rendererElement.getAttributeNode("hovered-color")),
                            Color.parseAndPack(rendererElement.getAttributeNode("disabled-color")));
                }
                case "texture" -> {
                    UIParsing.expectAttributes(rendererElement, "texture", "u", "v", "texture-width", "texture-height");
                    yield texture(
                            UIParsing.parseResourceLocation(rendererElement.getAttributeNode("texture")),
                            UIParsing.parseUnsignedInt(rendererElement.getAttributeNode("u")),
                            UIParsing.parseUnsignedInt(rendererElement.getAttributeNode("v")),
                            UIParsing.parseUnsignedInt(rendererElement.getAttributeNode("texture-width")),
                            UIParsing.parseUnsignedInt(rendererElement.getAttributeNode("texture-height")));
                }
                default -> throw new UIModelParsingException(
                        "Unknown button renderer '" + rendererElement.getNodeName() + "'");
            };
        }
    }
}
