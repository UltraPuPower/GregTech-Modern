package com.gregtechceu.gtceu.integration.rei;

import com.gregtechceu.gtceu.utils.GTMath;
import dev.emi.emi.api.stack.EmiStack;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.BuiltinEntryTypes;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * Custom EmiStack -> vanilla/forge/mod stack converters
 */
public class REIStackConverter {

    public static final Map<Class<?>, Converter<?>> CONVERTERS = new Reference2ReferenceOpenHashMap<>();

    public static final Converter<ItemStack> ITEM = register(ItemStack.class, new Converter<>() {
        @Override
        public @Nullable ItemStack convertFrom(EntryStack<?> stack) {
            EntryType<?> type = stack.getType();
            if (type != VanillaEntryTypes.ITEM) {
                return null;
            }
            return stack.castValue();
        }

        @Override
        public @NotNull EntryIngredient convertTo(ItemStack stack) {
            if (stack.isEmpty()) {
                return EntryIngredient.empty();
            }
            return EntryIngredient.of(EntryStacks.of(stack));
        }
    });
    public static final Converter<FluidStack> FLUID = register(FluidStack.class, new Converter<>() {
        @Override
        public @Nullable FluidStack convertFrom(EntryStack<?> stack) {
            EntryType<?> type = stack.getType();
            if (type != VanillaEntryTypes.FLUID) {
                return null;
            }
            dev.architectury.fluid.FluidStack fluidStack = stack.castValue();
            return new FluidStack(fluidStack.getFluid(), GTMath.saturatedCast(fluidStack.getAmount()), fluidStack.getTag());
        }

        @Override
        public @NotNull EntryIngredient convertTo(FluidStack stack) {
            if (stack.isEmpty()) {
                return EntryIngredient.empty();
            }
            return EntryIngredient.of(EntryStacks.of(
                    dev.architectury.fluid.FluidStack.create(stack.getRawFluid(), stack.getAmount(), stack.getTag())));
        }
    });

    public static <T> Converter<T> register(Class<T> clazz, Converter<T> converter) {
        CONVERTERS.put(clazz, converter);
        return converter;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> Converter<T> getForNullable(Class<T> clazz) {
        return (Converter<T>) CONVERTERS.get(clazz);
    }

    @NotNull
    public static <T> Optional<Converter<T>> getFor(Class<T> clazz) {
        return Optional.ofNullable(getForNullable(clazz));
    }

    public interface Converter<T> {
        @Nullable
        T convertFrom(EntryStack<?> stack);

        @NotNull
        EntryIngredient convertTo(T stack);
    }
}
