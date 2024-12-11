package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;

public class UITextureGroup extends TransformTexture {

    public UITexture[] textures;

    public UITextureGroup() {
        this(new ResourceTexture(null));
    }

    public UITextureGroup(UITexture... textures) {
        this.textures = textures;
    }

    public UITextureGroup setTextures(UITexture[] textures) {
        this.textures = textures;
        return this;
    }

    @Override
    public UITextureGroup setColor(int color) {
        for (UITexture texture : textures) {
            texture.setColor(color);
        }
        return this;
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        for (UITexture texture : textures) {
            texture.draw(graphics, mouseX, mouseY, x, y, width, height);
        }
    }

    @Override
    public void updateTick() {
        for (UITexture texture : textures) {
            texture.updateTick();
        }
    }

    @Override
    protected void drawSubAreaInternal(UIGuiGraphics graphics, int x, int y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        for (UITexture texture : textures) {
            texture.drawSubArea(graphics, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        }
    }
}
