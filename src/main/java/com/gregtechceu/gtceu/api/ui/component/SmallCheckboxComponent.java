package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.CursorStyle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.EventSource;
import com.gregtechceu.gtceu.api.ui.util.EventStream;
import com.gregtechceu.gtceu.api.ui.util.Observable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.util.Map;

public class SmallCheckboxComponent extends BaseUIComponent {

    public static final ResourceLocation TEXTURE = new ResourceLocation("owo", "textures/gui/smol_checkbox.png");

    protected final EventStream<OnChanged> checkedEvents = OnChanged.newStream();

    protected final Observable<@Nullable Component> label;
    protected boolean labelShadow = false;
    protected boolean checked = false;

    public SmallCheckboxComponent(Component label) {
        this.cursorStyle(CursorStyle.HAND);

        this.label = Observable.of(label);
        this.label.observe(text -> this.notifyParentIfMounted());
    }

    public SmallCheckboxComponent() {
        this(null);
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.label.get() != null) {
            graphics.drawString(Minecraft.getInstance().font, this.label.get(), this.x + 13 + 2, this.y + 3,
                    Color.WHITE.argb(), this.labelShadow);
        }

        graphics.blit(TEXTURE, this.x, this.y, 13, 13, 0, 0, 13, 13, 32, 16);
        if (this.checked) {
            graphics.blit(TEXTURE, this.x, this.y, 13, 13, 16, 0, 13, 13, 32, 16);
        }
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.label.get() != null ? 13 + 2 + Minecraft.getInstance().font.width(this.label.get()) : 13;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 13;
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        boolean result = super.onMouseDown(mouseX, mouseY, button);

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.toggle();
            return true;
        }

        return result;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        boolean result = super.onKeyPress(keyCode, scanCode, modifiers);

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
            this.toggle();
            return true;
        }

        return result;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    public void toggle() {
        this.checked(!this.checked);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public EventSource<OnChanged> onChanged() {
        return this.checkedEvents.source();
    }

    public SmallCheckboxComponent checked(boolean checked) {
        this.checked = checked;
        this.checkedEvents.sink().onChanged(this.checked);

        return this;
    }

    public boolean checked() {
        return checked;
    }

    public SmallCheckboxComponent label(Component label) {
        this.label.set(label);
        return this;
    }

    public Component label() {
        return this.label.get();
    }

    public SmallCheckboxComponent labelShadow(boolean labelShadow) {
        this.labelShadow = labelShadow;
        return this;
    }

    public boolean labelShadow() {
        return labelShadow;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "label", UIParsing::parseText, this::label);
        UIParsing.apply(children, "label-shadow", UIParsing::parseBool, this::labelShadow);
        UIParsing.apply(children, "checked", UIParsing::parseBool, this::checked);
    }

    public interface OnChanged {

        void onChanged(boolean nowChecked);

        static EventStream<OnChanged> newStream() {
            return new EventStream<>(subscribers -> value -> {
                for (var subscriber : subscribers) {
                    subscriber.onChanged(value);
                }
            });
        }
    }
}