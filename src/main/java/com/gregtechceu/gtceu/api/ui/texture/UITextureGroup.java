package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

public class UITextureGroup extends TransformTexture {

    public List<UITexture> children;

    protected UITextureGroup() {
        this(new UITexture[0]);
    }

    protected UITextureGroup(UITexture... textures) {
        this.children = new ArrayList<>(Arrays.asList(textures));
    }

    protected UITextureGroup(Collection<UITexture> textures) {
        this.children = new ArrayList<>(textures);
    }

    public UITextureGroup setChildren(UITexture[] children) {
        this.children = new ArrayList<>(Arrays.asList(children));
        return this;
    }

    public UITextureGroup setChildren(Collection<UITexture> textures) {
        this.children = new ArrayList<>(textures);
        return this;
    }

    public UITextureGroup child(UITexture texture) {
        this.children.add(texture);
        return this;
    }

    @Override
    public UITextureGroup setColor(int color) {
        for (UITexture texture : children) {
            texture.setColor(color);
        }
        return this;
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height) {
        for (UITexture child : children) {
            child.draw(graphics, mouseX, mouseY, x, y, width, height);
        }
    }

    @Override
    public void updateTick() {
        for (UITexture child : children) {
            child.updateTick();
        }
    }

    @Override
    protected void drawSubAreaInternal(UIGuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        for (UITexture child : children) {
            child.drawSubArea(graphics, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        }
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        final var components = UIParsing
                .get(children, "children", e -> UIParsing.<Element>allChildrenOfType(e, Node.ELEMENT_NODE))
                .orElse(Collections.emptyList());

        for (var child : components) {
            this.child(model.parseTexture(UITexture.class, child));
        }
    }

}
