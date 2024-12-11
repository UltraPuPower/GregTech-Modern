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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.mojang.blaze3d.systems.RenderSystem;
import org.w3c.dom.Element;

import java.util.Map;

@Accessors(fluent = true, chain = true)
public class TextureComponent extends BaseUIComponent {

    protected final UITexture texture;
    protected final int u, v;
    protected final int regionWidth, regionHeight;
    protected final int textureWidth, textureHeight;

    @Getter
    protected final AnimatableProperty<PositionedRectangle> visibleArea;
    @Getter
    @Setter
    protected boolean blend = false;

    protected TextureComponent(UITexture texture, int u, int v, int regionWidth, int regionHeight,
                               int textureWidth, int textureHeight) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

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
        this.texture.drawSubArea(graphics, visibleArea.x(), visibleArea.y(),
                width, height,
                (float) (this.u + visibleArea.x()) / visibleArea.y(),
                (float) (this.v + visibleArea.y()) / visibleArea.y(),
                (float) width / visibleArea.x(),
                (float) height / visibleArea.y());

        /*
        graphics.blit(this.texture,
                visibleArea.x(),
                visibleArea.y(),
                rightEdge - visibleArea.x(),
                bottomEdge - visibleArea.y(),
                this.u + visibleArea.x(),
                this.v + visibleArea.y(),
                rightEdge - visibleArea.x(),
                bottomEdge - visibleArea.y(),
                this.textureWidth, this.textureHeight);
        */

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
        UIParsing.expectAttributes(element, "texture");
        var textureId = UIParsing.parseResourceLocation(element.getAttributeNode("texture"));

        int u = 0, v = 0, regionWidth = 0, regionHeight = 0, textureWidth = 256, textureHeight = 256;
        if (element.hasAttribute("u")) {
            u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
        }

        if (element.hasAttribute("v")) {
            v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
        }

        if (element.hasAttribute("region-width")) {
            regionWidth = UIParsing.parseSignedInt(element.getAttributeNode("region-width"));
        }

        if (element.hasAttribute("region-height")) {
            regionHeight = UIParsing.parseSignedInt(element.getAttributeNode("region-height"));
        }

        if (element.hasAttribute("texture-width")) {
            textureWidth = UIParsing.parseSignedInt(element.getAttributeNode("texture-width"));
        }

        if (element.hasAttribute("texture-height")) {
            textureHeight = UIParsing.parseSignedInt(element.getAttributeNode("texture-height"));
        }

        // TODO UITexture parser
        return new TextureComponent(new ResourceTexture(textureId), u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }
}
