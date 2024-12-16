package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.container.WrappingParentUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.data.lang.LangHandler;

import com.lowdragmc.lowdraglib.LDLib;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EnumSelectorComponent<T extends Enum<T> & EnumSelectorComponent.SelectableEnum>
                                  extends WrappingParentUIComponent<CycleButtonComponent> {

    public interface SelectableEnum {

        String getTooltip();

        UITexture getIcon();
    }

    private final CycleButtonComponent buttonWidget;

    private final List<T> values;
    private final Consumer<T> onChanged;

    private int selected = 0;

    private BiFunction<T, UITexture, UITexture> textureSupplier = (value, texture) -> UITextures.group(
            GuiTextures.VANILLA_BUTTON, texture);

    private BiFunction<T, String, List<Component>> tooltipSupplier = (value, key) -> List
            .copyOf(LangHandler.getSingleOrMultiLang(key));

    public EnumSelectorComponent(Sizing horizontalSizing, Sizing verticalSizing, T[] values, T initialValue,
                                 Consumer<T> onChanged) {
        this(horizontalSizing, verticalSizing, Arrays.asList(values), initialValue, onChanged);
    }

    public EnumSelectorComponent(Sizing horizontalSizing, Sizing verticalSizing, List<T> values, T initialValue,
                                 Consumer<T> onChanged) {
        super(horizontalSizing, verticalSizing, null);

        this.values = values;
        this.onChanged = onChanged;

        this.buttonWidget = new CycleButtonComponent(values.size(), this::getTexture,
                this::onSelected);
        this.buttonWidget.sizing(this.horizontalSizing().get(), this.verticalSizing().get());
        this.child(buttonWidget);

        setSelected(initialValue);
    }

    /*
     * @Override
     * public void writeInitialData(FriendlyByteBuf buffer) {
     * super.writeInitialData(buffer);
     * buffer.writeInt(selected);
     * }
     * 
     * @Override
     * public void readInitialData(FriendlyByteBuf buffer) {
     * super.readInitialData(buffer);
     * onSelected(buffer.readInt());
     * }
     */

    public T getCurrentValue() {
        return values.get(selected);
    }

    private UITexture getTexture(int selected) {
        var selectedValue = values.get(selected);
        return textureSupplier.apply(selectedValue, selectedValue.getIcon());
    }

    private void onSelected(int selected) {
        T selectedValue = values.get(selected);
        setSelected(selectedValue);
    }

    public EnumSelectorComponent<T> setTextureSupplier(BiFunction<T, UITexture, UITexture> textureSupplier) {
        this.textureSupplier = textureSupplier;

        T selectedValue = getCurrentValue();
        buttonWidget.current = textureSupplier.apply(selectedValue, selectedValue.getIcon());

        return this;
    }

    public EnumSelectorComponent<T> setTooltipSupplier(BiFunction<T, String, List<Component>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;

        return this;
    }

    public void setSelected(@NotNull T value) {
        var selectedIndex = values.indexOf(value);

        if (selectedIndex == -1)
            throw new NoSuchElementException(value + " is not a possible value for this selector.");

        this.selected = selectedIndex;
        this.buttonWidget.setIndex(selectedIndex);

        updateTooltip();

        onChanged.accept(value);
    }

    private void updateTooltip() {
        if (!LDLib.isRemote())
            return;

        T selectedValue = getCurrentValue();
        buttonWidget.tooltip(tooltipSupplier.apply(selectedValue, selectedValue.getTooltip()));
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
    }
}
