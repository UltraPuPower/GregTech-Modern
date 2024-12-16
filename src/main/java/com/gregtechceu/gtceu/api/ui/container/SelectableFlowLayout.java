package com.gregtechceu.gtceu.api.ui.container;

import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Accessors(fluent = true, chain = true)
public class SelectableFlowLayout extends FlowLayout {

    @Getter
    @Setter
    protected boolean isSelected;
    @Setter
    protected UITexture selectedTexture;
    @Setter
    protected Consumer<SelectableFlowLayout> onSelected;
    @Setter
    protected Consumer<SelectableFlowLayout> onUnSelected;
    protected final BooleanSupplier selectionSupplier;

    public SelectableFlowLayout(Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm,
                                BooleanSupplier selectionSupplier) {
        super(horizontalSizing, verticalSizing, algorithm);
        this.selectionSupplier = selectionSupplier;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (selectionSupplier.getAsBoolean()) {
            onSelected();
        } else {
            onUnSelected();
        }
        return super.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        if (isSelected && selectedTexture != null) {
            selectedTexture.draw(graphics, mouseX, mouseY, x(), y(), width(), height());
        }
    }

    public void onSelected() {
        isSelected = true;
        if (onSelected != null) onSelected.accept(this);
    }

    public void onUnSelected() {
        isSelected = false;
        if (onUnSelected != null) onUnSelected.accept(this);
    }
}
