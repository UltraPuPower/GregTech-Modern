package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.api.ui.component.ButtonComponent;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2023/7/24
 * @implNote IFancyConfiguratorButton
 */
public interface IFancyConfiguratorButton extends IFancyConfigurator {

    void onClick(ButtonComponent clickData);

    @Override
    default Component getTitle() {
        throw new NotImplementedException();
    }

    @Override
    default UIComponent createConfigurator() {
        throw new NotImplementedException();
    }

    @Accessors(chain = true)
    class Toggle implements IFancyConfiguratorButton {

        private final UITexture base;
        private final UITexture pressed;
        private final BiConsumer<ButtonComponent, Boolean> onClick;
        private final BooleanSupplier booleanSupplier;
        boolean isPressed;
        @Setter
        Function<Boolean, List<Component>> tooltipsSupplier = isPressed -> Collections.emptyList();

        public Toggle(UITexture base, UITexture pressed, BooleanSupplier booleanSupplier,
                      BiConsumer<ButtonComponent, Boolean> onClick) {
            this.base = base;
            this.pressed = pressed;
            this.booleanSupplier = booleanSupplier;
            this.onClick = onClick;
        }

        @Override
        public List<Component> getTooltips() {
            return tooltipsSupplier.apply(isPressed);
        }

        @Override
        public void detectAndSendChange(BiConsumer<Integer, Consumer<FriendlyByteBuf>> sender) {
            var newIsPressed = booleanSupplier.getAsBoolean();
            if (newIsPressed != isPressed) {
                isPressed = newIsPressed;
                sender.accept(0, buf -> buf.writeBoolean(isPressed));
            }
        }

        @Override
        public void readUpdateInfo(int id, FriendlyByteBuf buf) {
            if (id == 0) {
                isPressed = buf.readBoolean();
            }
        }

        @Override
        public void writeInitialData(FriendlyByteBuf buffer) {
            this.isPressed = booleanSupplier.getAsBoolean();
            buffer.writeBoolean(this.isPressed);
        }

        @Override
        public void readInitialData(FriendlyByteBuf buffer) {
            this.isPressed = buffer.readBoolean();
        }

        @Override
        public UITexture getIcon() {
            return isPressed ? pressed : base;
        }

        @Override
        public void onClick(ButtonComponent clickData) {
            onClick.accept(clickData, !isPressed);
        }
    }
}
