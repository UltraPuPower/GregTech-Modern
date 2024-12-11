package com.gregtechceu.gtceu.api.ui.fancy;

import com.gregtechceu.gtceu.api.ui.texture.UITexture;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2023/6/28
 * @implNote IFancyConfigurator
 */
public interface IFancyTooltip {

    UITexture getFancyTooltipIcon();

    List<Component> getFancyTooltip();

    default boolean showFancyTooltip() {
        return true;
    }

    @Nullable
    default TooltipComponent getFancyComponent() {
        return null;
    }

    record Basic(Supplier<UITexture> icon, Supplier<List<Component>> content, Supplier<Boolean> predicate,
                 Supplier<TooltipComponent> componentSupplier)
            implements IFancyTooltip {

        @Override
        public UITexture getFancyTooltipIcon() {
            return icon.get();
        }

        @Override
        public List<Component> getFancyTooltip() {
            return content.get();
        }

        @Override
        public @Nullable TooltipComponent getFancyComponent() {
            return componentSupplier.get();
        }

        @Override
        public boolean showFancyTooltip() {
            return predicate.get();
        }
    }
}
