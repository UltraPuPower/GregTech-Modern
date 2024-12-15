package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import com.gregtechceu.gtceu.api.ui.texture.UITexture;
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
    @Getter
    @Setter
    protected boolean blend = false;

    protected TextureComponent(UITexture texture) {
        this.texture = texture;
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

        this.texture.draw(graphics, mouseX, mouseY, x(), this.y(), width(), height());

        if (this.blend) {
            RenderSystem.disableBlend();
        }

        pose.popPose();
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.expectChildren(element, children, "value");
        this.texture = model.parseTexture(UITexture.class, children.get("value"));

        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);
    }
}
