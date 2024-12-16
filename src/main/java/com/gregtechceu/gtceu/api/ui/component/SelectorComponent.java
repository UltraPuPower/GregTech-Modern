package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.ScrollContainer;
import com.gregtechceu.gtceu.api.ui.container.SelectableFlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.*;
import com.gregtechceu.gtceu.api.ui.texture.TextTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Accessors(fluent = true, chain = true)
public class SelectorComponent extends FlowLayout {

    protected List<SelectableFlowLayout> selectables;
    protected List<String> candidates;
    protected String currentValue;
    protected int maxCount = 5;
    protected Color fontColor = Color.BLACK;
    protected boolean showUp;
    protected boolean isShow;
    protected Surface popUpTexture = Surface.flat(0xAA000000);
    @Setter
    private Supplier<List<String>> candidatesSupplier;
    @Setter
    private Supplier<String> supplier;
    @Setter
    private Consumer<String> onChanged;
    public final TextTexture textTexture;
    protected final ScrollContainer<FlowLayout> popUp;
    protected final ButtonComponent button;

    protected boolean isInit = false;

    public SelectorComponent() {
        this(Sizing.fixed(60), Sizing.fixed(15), List.of(), -1);
    }

    public SelectorComponent(Sizing horizontalSizing, Sizing verticalSizing, List<String> candidates, int fontColor) {
        super(horizontalSizing, verticalSizing, Algorithm.VERTICAL);
        this.button = new ButtonComponent(Component.empty(), d -> {
            if (d.isClientSide) setShow(!isShow);
        }).renderer(ButtonComponent.Renderer.EMPTY)
                .configure(c -> {
                    c.positioning(Positioning.absolute(0, 0))
                            .sizing(horizontalSizing, verticalSizing);
                });
        this.candidates = candidates;
        this.selectables = new ArrayList<>();
        this.child(button);
        this.child(UIComponents
                .texture(textTexture = UITextures.text(Component.empty()).color(fontColor).width(width)
                        .textType(TextTexture.TextType.ROLL))
                .positioning(Positioning.absolute(0, 1))
                .sizing(horizontalSizing, verticalSizing));
        this.child(popUp = UIContainers.verticalScroll(horizontalSizing, Sizing.content(),
                UIContainers.horizontalFlow(Sizing.content(), Sizing.content()))
                .configure(c -> {
                    c.positioning(Positioning.across(0, 100));
                }));
        popUp.surface(popUpTexture);
        popUp.enabled(false);
        currentValue = "";

        isInit = true;
        updateLayout();
    }

    protected void updateLayout() {
        super.updateLayout();
        if (!isInit) return;

        int height = Math.min(maxCount, candidates.size()) * 15;
        popUp.child().clearChildren();
        selectables.clear();
        popUp.sizing(Sizing.fill(), Sizing.content());
        if (showUp) {
            popUp.moveTo(this.x(), -height);
        } else {
            popUp.moveTo(this.x(), this.height());
        }
        if (candidates.size() > maxCount) {
            popUp.scrollbarThickness(4).scrollbar(ScrollContainer.Scrollbar.flat(Color.BLACK));
        }
        int width = candidates.size() > maxCount ? this.width() - 4 : this.width();
        for (String candidate : candidates) {
            SelectableFlowLayout select = new SelectableFlowLayout(Sizing.fixed(width), Sizing.fixed(15),
                    Algorithm.HORIZONTAL, () -> Objects.equals(currentValue, candidate));
            select.child(UIComponents.texture(UITextures.text(Component.literal(candidate))
                    .color(fontColor.argb())
                    .width(width)
                    .textType(TextTexture.TextType.ROLL))
                    .sizing(Sizing.fixed(width), Sizing.fixed(15)));
            select.selectedTexture(UITextures.colorBorder(Color.BLACK, -1));
            select.onSelected(s -> {
                value(candidate);
                if (onChanged != null) {
                    onChanged.accept(candidate);
                }
                sendMessage(2, buffer -> buffer.writeUtf(candidate));
                setShow(false);
            });
            popUp.child().child(select);
            selectables.add(select);
            y += 15;
        }
        popUp.scrollTo(0);
    }

    public SelectorComponent maxCount(int maxCount) {
        this.maxCount = maxCount;
        updateLayout();
        return this;
    }

    public SelectorComponent isUp(boolean isUp) {
        this.showUp = isUp;
        updateLayout();
        return this;
    }

    public SelectorComponent fontColor(Color fontColor) {
        this.fontColor = fontColor;
        updateLayout();
        return this;
    }

    public SelectorComponent value(String value) {
        if (!value.equals(currentValue)) {
            currentValue = value;
            int index = candidates.indexOf(value);
            textTexture.text(Component.literal(value));
            for (int i = 0; i < selectables.size(); i++) {
                selectables.get(i).isSelected(index == i);
            }
        }
        return this;
    }

    public SelectorComponent candidates(List<String> candidates) {
        this.candidates = candidates;
        updateLayout();
        return this;
    }

    public SelectorComponent buttonSurface(Surface surface) {
        super.surface(surface);
        return this;
    }

    public SelectorComponent surface(Surface surface) {
        popUpTexture = surface;
        popUp.surface(surface);
        return this;
    }

    @Override
    public SelectorComponent width(int width) {
        super.width(width);
        button.setWidth(width);
        updateLayout();
        return this;
    }

    @Override
    public BaseUIComponent height(int height) {
        super.height(height);
        button.setHeight(height);
        updateLayout();
        return this;
    }

    public void setShow(boolean isShow) {
        if (isShow && focusHandler() != null) {
            focusHandler().focus(this, FocusSource.MOUSE_CLICK);
        }
        this.isShow = isShow;
        popUp.enabled(isShow);
    }

    public String getValue() {
        return currentValue;
    }

    @Override
    public boolean isMouseOverElement(double mouseX, double mouseY) {
        return super.isMouseOverElement(mouseX, mouseY) || (isShow && popUp.isMouseOverElement(mouseX, mouseY));
    }

    @Override
    public @Nullable UIComponent getHoveredComponent(int mouseX, int mouseY) {
        return isMouseOverElement(mouseX, mouseY) ? this : null;
    }

    @Override
    public void onFocusLost() {
        setShow(false);
    }

    @Override
    protected void parentUpdate(float delta, int mouseX, int mouseY) {
        super.parentUpdate(delta, mouseX, mouseY);

        if (candidatesSupplier != null) {
            var latest = candidatesSupplier.get();
            if (!latest.equals(candidates)) {
                candidates(latest);
            }
        }
        if (supplier != null) {
            value(supplier.get());
        }
        if (focusHandler() != null) {
            var handler = focusHandler();
            if (handler.focused() != null && handler.focused().parent() == this) {
                handler.focus(this, FocusSource.MOUSE_CLICK);
            }
        }
    }

    /*
     * @Override
     * public void writeInitialData(FriendlyByteBuf buffer) {
     * super.writeInitialData(buffer);
     * if (supplier != null) {
     * value(supplier.get());
     * }
     * buffer.m_130070_(currentValue);
     * }
     * 
     * @Override
     * public void readInitialData(FriendlyByteBuf buffer) {
     * super.readInitialData(buffer);
     * value(buffer.m_130277_());
     * }
     */

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        boolean lastVisible = popUp.enabled();
        popUp.enabled(false);
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
        popUp.enabled(lastVisible);

        if (isShow) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200);
            popUp.draw(graphics, mouseX, mouseY, partialTicks, delta);
            graphics.pose().popPose();
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (!super.onMouseDown(mouseX, mouseY, button)) {
            if (focusHandler() != null) {
                focusHandler().focus(null, null);
            }
            return false;
        }
        return true;
    }

    @Override
    public void receiveMessage(int id, FriendlyByteBuf buffer) {
        super.receiveMessage(id, buffer);
        if (id == 2) {
            value(buffer.readUtf());
            if (onChanged != null) {
                onChanged.accept(getValue());
            }
        } else if (id == 3) {
            value(buffer.readUtf());
        } else if (id == 4) {
            var size = buffer.readVarInt();
            List<String> latest = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                latest.add(buffer.readUtf());
            }
            candidates(latest);
        }
    }
}
