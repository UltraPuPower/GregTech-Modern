package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;

import org.w3c.dom.Element;

public class SpriteComponent extends BaseUIComponent {

    protected final TextureAtlasSprite sprite;

    protected SpriteComponent(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.sprite.contents().width();
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.sprite.contents().height();
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        // SpriteUtilInvoker.markSpriteActive(this.sprite);
        graphics.blit(this.x, this.y, 0, this.width, this.height, this.sprite);
    }

    public static SpriteComponent parse(Element element) {
        UIParsing.expectAttributes(element, "atlas", "sprite");

        var atlas = UIParsing.parseResourceLocation(element.getAttributeNode("atlas"));
        var sprite = UIParsing.parseResourceLocation(element.getAttributeNode("sprite"));

        return UIComponents.sprite(new Material(atlas, sprite));
    }
}
