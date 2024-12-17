package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import net.minecraft.resources.ResourceLocation;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.w3c.dom.Element;

import java.util.Map;

@Accessors(fluent = true, chain = true)
public class ResourceTexture extends TransformTexture {

    public ResourceLocation imageLocation;
    public float offsetX;
    public float offsetY;
    public float imageWidth;
    public float imageHeight;
    @Setter
    protected int color = -1;

    protected ResourceTexture(ResourceLocation imageLocation, float offsetX, float offsetY, float width, float height) {
        this.imageLocation = imageLocation;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    protected ResourceTexture(ResourceLocation imageLocation) {
        this(imageLocation, 0, 0, 1, 1);
    }

    public ResourceTexture getSubTexture(float offsetX, float offsetY, float width, float height) {
        return new ResourceTexture(imageLocation,
                this.offsetX + (imageWidth * offsetX),
                this.offsetY + (imageHeight * offsetY),
                this.imageWidth * width,
                this.imageHeight * height);
    }

    public ResourceTexture getSubTexture(double offsetX, double offsetY, double width, double height) {
        return new ResourceTexture(imageLocation,
                this.offsetX + (float) (imageWidth * offsetX),
                this.offsetY + (float) (imageHeight * offsetY),
                this.imageWidth * (float) width,
                this.imageHeight * (float) height);
    }

    public ResourceTexture copy() {
        return getSubTexture(0, 0, 1, 1);
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width,
                                float height) {
        drawSubAreaInternal(graphics, x, y, width, height, 0, 0, 1, 1);
    }

    @Override
    protected void drawSubAreaInternal(UIGuiGraphics graphics, float x, float y, float width, float height,
                                       float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        if (imageLocation == null || imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        float imageU = this.offsetX + (this.imageWidth * drawnU);
        float imageV = this.offsetY + (this.imageHeight * drawnV);
        float imageWidth = this.imageWidth * drawnWidth;
        float imageHeight = this.imageHeight * drawnHeight;

        graphics.blit(imageLocation, x, y, imageU, imageV, imageWidth, imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
    }

    public static ResourceTexture parse(Element element) {
        UIParsing.expectAttributes(element, "texture");
        var textureId = UIParsing.parseResourceLocation(element.getAttributeNode("texture"));

        int u = 0, v = 0, textureWidth = 256, textureHeight = 256;
        if (element.hasAttribute("u")) {
            u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
        }

        if (element.hasAttribute("v")) {
            v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
        }

        if (element.hasAttribute("texture-width")) {
            textureWidth = UIParsing.parseSignedInt(element.getAttributeNode("texture-width"));
        }

        if (element.hasAttribute("texture-height")) {
            textureHeight = UIParsing.parseSignedInt(element.getAttributeNode("texture-height"));
        }

        return new ResourceTexture(textureId, u, v, textureWidth, textureHeight);
    }
}
