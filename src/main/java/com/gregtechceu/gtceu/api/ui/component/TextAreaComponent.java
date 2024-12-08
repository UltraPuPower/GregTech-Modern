package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.core.CursorStyle;
import com.gregtechceu.gtceu.api.ui.core.Size;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.EventSource;
import com.gregtechceu.gtceu.api.ui.util.EventStream;
import com.gregtechceu.gtceu.api.ui.util.Observable;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.MultilineEditBoxAccessor;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.MultilineTextFieldAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.Consumer;

public class TextAreaComponent extends MultiLineEditBox {

    protected final Observable<String> textValue = Observable.of("");
    protected final EventStream<OnChanged> changedEvents = OnChanged.newStream();
    protected final MultilineTextField editBox;

    protected final Observable<Boolean> displayCharCount = Observable.of(false);
    protected final Observable<Integer> maxLines = Observable.of(-1);

    protected TextAreaComponent(Sizing horizontalSizing, Sizing verticalSizing) {
        super(Minecraft.getInstance().font, 0, 0, 0, 0, Component.empty(), Component.empty());
        this.editBox = ((MultilineEditBoxAccessor) this).gtceu$getTextField();
        this.sizing(horizontalSizing, verticalSizing);

        this.textValue.observe(this.changedEvents.sink()::onChanged);
        Observable.observeAll(this.widgetWrapper()::notifyParentIfMounted, this.displayCharCount, this.maxLines);

        super.setValueListener(s -> {
            this.textValue.set(s);

            if (this.maxLines.get() < 0) return;
            this.widgetWrapper().notifyParentIfMounted();
        });
    }

    @Override
    @Deprecated(forRemoval = true)
    public void setValueListener(@NotNull Consumer<String> changeListener) {
        GTCEu.LOGGER.warn("setChangeListener stub on TextAreaComponent invoked");
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.cursorStyle(this.scrollbarVisible() && mouseX >= this.getX() + this.width - 9 ? CursorStyle.NONE :
                CursorStyle.TEXT);
    }

    @Override
    protected void renderDecorations(GuiGraphics graphics) {
        this.height -= 1;

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(-9, 1, 0);

        int previousMaxLength = this.editBox.characterLimit();
        this.editBox.setCharacterLimit(Integer.MAX_VALUE);

        super.renderDecorations(graphics);

        this.editBox.setCharacterLimit(previousMaxLength);

        pose.popPose();
        this.height += 1;

        if (this.displayCharCount.get()) {
            var text = this.editBox.hasCharacterLimit() ?
                    Component.translatable("gui.multiLineEditBox.character_limit", this.editBox.value().length(),
                            this.editBox.characterLimit()) :
                    Component.literal(String.valueOf(this.editBox.value().length()));

            var textRenderer = Minecraft.getInstance().font;
            graphics.drawCenteredString(textRenderer, text, this.getX() + this.width - textRenderer.width(text),
                    this.getY() + this.height + 3, 0xa0a0a0);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.width -= 9;
        var result = super.mouseClicked(mouseX, mouseY, button);
        this.width += 9;

        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == GLFW.GLFW_KEY_TAB) {
            this.editBox.insertText("    ");
            return true;
        } else {
            return result;
        }
    }

    @Override
    public TextAreaComponent inflate(Size space) {
        super.inflate(space);

        int cursor = this.editBox.cursor();
        int selection = ((MultilineTextFieldAccessor) this.editBox).gtceu$getSelectCursor();

        ((MultilineTextFieldAccessor) this.editBox).gtceu$setWidth(this.width() - this.totalInnerPadding() - 9);
        this.editBox.setValue(this.getValue());

        super.inflate(space);
        this.editBox.setValue(this.getValue());

        this.editBox.seekCursor(Whence.ABSOLUTE, cursor);
        ((MultilineTextFieldAccessor) this.editBox).gtceu$setSelectCursor(selection);
        return this;
    }

    public EventSource<OnChanged> onChanged() {
        return changedEvents.source();
    }

    public TextAreaComponent maxLines(int maxLines) {
        this.maxLines.set(maxLines);
        return this;
    }

    public int maxLines() {
        return this.maxLines.get();
    }

    public TextAreaComponent displayCharCount(boolean displayCharCount) {
        this.displayCharCount.set(displayCharCount);
        return this;
    }

    public boolean displayCharCount() {
        return this.displayCharCount.get();
    }

    public TextAreaComponent text(String text) {
        this.setValue(text);
        return this;
    }

    @Override
    public int heightOffset() {
        return this.displayCharCount.get() ? -12 : 0;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "display-char-count", UIParsing::parseBool, this::displayCharCount);
        UIParsing.apply(children, "max-length", UIParsing::parseUnsignedInt, this::setCharacterLimit);
        UIParsing.apply(children, "max-lines", UIParsing::parseUnsignedInt, this::maxLines);
        UIParsing.apply(children, "text", $ -> $.getTextContent().strip(), this::text);
    }

    public interface OnChanged {

        void onChanged(String value);

        static EventStream<OnChanged> newStream() {
            return new EventStream<>(subscribers -> value -> {
                for (var subscriber : subscribers) {
                    subscriber.onChanged(value);
                }
            });
        }
    }
}
