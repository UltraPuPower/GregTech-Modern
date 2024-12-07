package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.core.mixins.ui.accessor.AbstractWidgetAccessor;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.EditBoxAccessor;
import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class VanillaWidgetComponent extends BaseUIComponent {

    private final AbstractWidget widget;

    private float time = 0f;
    private @Nullable Runnable tickCallback = null;

    protected VanillaWidgetComponent(AbstractWidget widget) {
        this.widget = widget;

        this.horizontalSizing.set(Sizing.fixed(this.widget.getWidth()));
        this.verticalSizing.set(Sizing.fixed(this.widget.getHeight()));

        if (widget instanceof EditBox textField) {
            this.margins(Insets.none());
            this.tickCallback = textField::tick;
        }

        if (widget instanceof MultiLineEditBox editBox) {
            this.tickCallback = editBox::tick;
        }
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        if (this.tickCallback == null) return;

        this.time += delta;
        while (this.time >= 1f) {
            this.time -= 1f;
            this.tickCallback.run();
        }
    }

    @Override
    public void mount(ParentUIComponent parent, int x, int y) {
        super.mount(parent, x, y);
        this.applyToWidget();
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        if (this.widget instanceof Button || this.widget instanceof Checkbox || this.widget instanceof SliderComponent) {
            return 20;
        } else if (this.widget instanceof EditBox textField) {
            if (((EditBoxAccessor) textField).ui$drawsBackground()) {
                return 20;
            } else {
                return 9;
            }
        } else if (this.widget instanceof TextAreaComponent textArea && textArea.maxLines() > 0) {
            return Mth.clamp(textArea.getHeight() / 9 + 1, 2, textArea.maxLines()) * 9 + (textArea.displayCharCount() ? 9 + 12 : 9);
        } else {
            throw new UnsupportedOperationException(this.widget.getClass().getSimpleName() + " does not support Sizing.content() on the vertical axis");
        }
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        if (this.widget instanceof Button button) {
            return Minecraft.getInstance().font.width(button.getMessage()) + 8;
        } else if (this.widget instanceof Checkbox checkbox) {
            return Minecraft.getInstance().font.width(checkbox.getMessage()) + 24;
        } else {
            throw new UnsupportedOperationException(this.widget.getClass().getSimpleName() + " does not support Sizing.content() on the horizontal axis");
        }
    }

    @Override
    public BaseUIComponent margins(Insets margins) {
        if (widget instanceof EditBox) {
            return super.margins(margins.add(1, 1, 1, 1));
        } else {
            return super.margins(margins);
        }
    }

    @Override
    public void inflate(Size space) {
        super.inflate(space);
        this.applyToWidget();
    }

    @Override
    public void updateX(int x) {
        super.updateX(x);
        this.applyToWidget();
    }

    @Override
    public void updateY(int y) {
        super.updateY(y);
        this.applyToWidget();
    }

    private void applyToWidget() {
        var accessor = (AbstractWidgetAccessor) this.widget;

        accessor.ui$setX(this.x + this.widget.xOffset());
        accessor.ui$setY(this.y + this.widget.yOffset());

        accessor.ui$setWidth(this.width + this.widget.widthOffset());
        accessor.ui$setHeight(this.height + this.widget.heightOffset());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends UIComponent> C configure(Consumer<C> closure) {
        try {
            this.runAndDeferEvents(() -> closure.accept((C) this.widget));
        } catch (ClassCastException theUserDidBadItWasNotMyFault) {
            throw new IllegalArgumentException(
                    "Invalid target class passed when configuring component of type " + this.getClass().getSimpleName(),
                    theUserDidBadItWasNotMyFault
            );
        }

        return (C) this.widget;
    }

    @Override
    public void notifyParentIfMounted() {
        super.notifyParentIfMounted();
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        this.widget.render(graphics, mouseX, mouseY, 0);
    }

    @Override
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        return this.widget.visible && this.widget.active && super.shouldDrawTooltip(mouseX, mouseY);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.widget.mouseClicked(this.x + mouseX, this.y + mouseY, button)
                | super.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return this.widget.mouseReleased(this.x + mouseX, this.y + mouseY, button)
                | super.onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.widget.mouseScrolled(this.x + mouseX, this.y + mouseY, amount)
                | super.onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        return this.widget.mouseDragged(this.x + mouseX, this.y + mouseY, button, deltaX, deltaY)
                | super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.widget.charTyped(chr, modifiers)
                | super.onCharTyped(chr, modifiers);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.widget.keyPressed(keyCode, scanCode, modifiers)
                | super.onKeyPress(keyCode, scanCode, modifiers);
    }

}
