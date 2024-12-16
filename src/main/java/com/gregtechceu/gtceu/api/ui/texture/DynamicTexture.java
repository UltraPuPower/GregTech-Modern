package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;

import lombok.Setter;

import java.util.function.Supplier;

public class DynamicTexture implements UITexture {

    @Setter
    protected Supplier<UITexture> textureSupplier;

    protected DynamicTexture(Supplier<UITexture> rendererSupplier) {
        this.textureSupplier = rendererSupplier;
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height) {
        textureSupplier.get().draw(graphics, mouseX, mouseY, x, y, width, height);
    }
}
