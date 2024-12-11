package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.AnimatableProperty;
import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.texture.NinePatchTexture;

import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.w3c.dom.Element;

import java.util.Map;

@Accessors(fluent = true, chain = true)
public class NinePatchTextureComponent extends BaseUIComponent {

    protected final ResourceLocation texture;

    @Getter
    protected final AnimatableProperty<PositionedRectangle> visibleArea;
    @Getter
    @Setter
    protected boolean blend = false;

    protected NinePatchTextureComponent(ResourceLocation texture) {
        this.texture = texture;
        this.visibleArea = AnimatableProperty.of(PositionedRectangle.of(this.x, this.y, this.width, this.height));
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.width;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.height;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.visibleArea.update(delta);
    }

    @Override
    public NinePatchTextureComponent x(int x) {
        super.x(x);
        this.visibleArea(PositionedRectangle.of(x, this.y, this.width, this.height));
        return this;
    }

    @Override
    public NinePatchTextureComponent y(int y) {
        super.y(y);
        this.visibleArea(PositionedRectangle.of(this.x, y, this.width, this.height));
        return this;
    }

    @Override
    public void applySizing() {
        super.applySizing();
        this.visibleArea(PositionedRectangle.of(0, 0, this.width, this.height));
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        NinePatchTexture.draw(texture, graphics, visibleArea.get());
    }

    public NinePatchTextureComponent visibleArea(PositionedRectangle visibleArea) {
        super.x(visibleArea.x());
        super.y(visibleArea.y());
        this.width(visibleArea.width());
        this.height(visibleArea.height());
        this.visibleArea.set(visibleArea);
        return this;
    }

    public NinePatchTextureComponent resetVisibleArea() {
        this.visibleArea(PositionedRectangle.of(0, 0, this.width, this.height));
        return this;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);

        if (children.containsKey("visible-area")) {
            var areaChildren = UIParsing.childElements(children.get("visible-area"));

            int x = 0, y = 0, width = this.width, height = this.height;
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

    public static NinePatchTextureComponent parse(Element element) {
        UIParsing.expectAttributes(element, "texture");
        var textureId = UIParsing.parseResourceLocation(element.getAttributeNode("texture"));
        return new NinePatchTextureComponent(textureId);
    }
}
