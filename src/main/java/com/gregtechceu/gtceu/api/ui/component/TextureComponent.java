package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.AnimatableProperty;
import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import com.gregtechceu.gtceu.api.ui.texture.ResourceTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.mojang.blaze3d.systems.RenderSystem;
import org.w3c.dom.Element;

import java.util.Map;

@Accessors(fluent = true, chain = true)
public class TextureComponent extends BaseUIComponent {

    @Setter
    protected UITexture texture;
    protected final int regionWidth, regionHeight;

    @Getter
    protected final AnimatableProperty<PositionedRectangle> visibleArea;
    @Getter
    @Setter
    protected boolean blend = false;

    protected TextureComponent(UITexture texture, int regionWidth, int regionHeight) {
        this.texture = texture;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;

        this.visibleArea = AnimatableProperty.of(PositionedRectangle.of(0, 0, this.regionWidth, this.regionHeight));
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.regionWidth;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.regionHeight;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.visibleArea.update(delta);
    }

    @Override
    public void tick() {
        super.tick();
        texture.updateTick();
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (texture == null) {
            return;
        }

        RenderSystem.enableDepthTest();

        if (this.blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(this.width / (float) this.regionWidth, this.height / (float) this.regionHeight, 0);

        var visibleArea = this.visibleArea.get();

        int width = Math.min(visibleArea.width(), regionWidth);
        int height = Math.min(visibleArea.height(), regionHeight);

        this.texture.draw(graphics, mouseX, mouseY, visibleArea.x() + this.x(), visibleArea.y() + this.y(), width, height);

        if (this.blend) {
            RenderSystem.disableBlend();
        }

        pose.popPose();
    }

    public TextureComponent visibleArea(PositionedRectangle visibleArea) {
        this.visibleArea.set(visibleArea);
        return this;
    }

    public TextureComponent resetVisibleArea() {
        this.visibleArea(PositionedRectangle.of(0, 0, this.regionWidth, this.regionHeight));
        return this;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        var textureElement = children.get("texture");
        this.texture.parseProperties(model, textureElement, UIParsing.childElements(textureElement));

        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);

        if (children.containsKey("visible-area")) {
            var areaChildren = UIParsing.childElements(children.get("visible-area"));

            int x = 0, y = 0, width = this.regionWidth, height = this.regionHeight;
            if (areaChildren.containsKey("x")) {
                x = UIParsing.parseSignedInt(areaChildren.get("x"));
            }

            if (areaChildren.containsKey("y")) {
                y = UIParsing.parseSignedInt(areaChildren.get("y"));
            }

            if (areaChildren.containsKey("width")) {
                width = UIParsing.parseSignedInt(areaChildren.get("width"));
            }

            if (areaChildren.containsKey("height")) {
                height = UIParsing.parseSignedInt(areaChildren.get("height"));
            }

            this.visibleArea(PositionedRectangle.of(x, y, width, height));
        }
    }

    public static TextureComponent parse(Element element) {
        var children = UIParsing.childElements(element);
        UIParsing.expectChildren(element, children, "texture");

        var child = children.get("texture");
        var texture = UIParsing.getTextureFactory(child).apply(child);

        int regionWidth = 0, regionHeight = 0;
        if (element.hasAttribute("region-width")) {
            regionWidth = UIParsing.parseSignedInt(element.getAttributeNode("region-width"));
        }

        if (element.hasAttribute("region-height")) {
            regionHeight = UIParsing.parseSignedInt(element.getAttributeNode("region-height"));
        }

        return new TextureComponent(texture, regionWidth, regionHeight);
    }
}
