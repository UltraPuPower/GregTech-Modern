package com.gregtechceu.gtceu.common.item.armor;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.item.armor.ArmorComponentItem;
import com.gregtechceu.gtceu.api.item.armor.ArmorUtils;
import com.gregtechceu.gtceu.api.item.armor.IArmorLogic;
import com.gregtechceu.gtceu.api.item.component.*;
import com.gregtechceu.gtceu.api.item.component.forge.IComponentCapability;
import com.gregtechceu.gtceu.api.item.datacomponents.GTArmor;
import com.gregtechceu.gtceu.api.misc.FluidRecipeHandler;
import com.gregtechceu.gtceu.api.misc.IgnoreEnergyRecipeHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.gregtechceu.gtceu.data.tag.GTDataComponents;
import com.gregtechceu.gtceu.utils.GradientUtil;
import com.gregtechceu.gtceu.utils.input.KeyBind;

import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PowerlessJetpack implements IArmorLogic, IJetpack, IItemHUDProvider {

    // Map of FluidIngredient -> burn time
    public static final AbstractObject2IntMap<FluidIngredient> FUELS = new Object2IntOpenHashMap<>();
    public static final int tankCapacity = 16000;

    private FluidIngredient currentFuel = FluidIngredient.EMPTY;
    private FluidIngredient previousFuel = FluidIngredient.EMPTY;
    private int burnTimer = 0;

    @OnlyIn(Dist.CLIENT)
    private ArmorUtils.ModularHUD HUD;

    public PowerlessJetpack() {
        if (Platform.isClient())
            HUD = new ArmorUtils.ModularHUD();
    }

    @Override
    public void onArmorTick(Level world, Player player, @NotNull ItemStack stack) {
        if (FluidUtil.getFluidHandler(stack) == null) return;

        GTArmor data = stack.get(GTDataComponents.ARMOR_DATA);
        if (data == null) {
            return;
        }
        burnTimer = data.burnTimer();
        byte toggleTimer = data.toggleTimer();
        boolean hoverMode = data.hover();
        boolean jetpackEnabled = data.enabled();

        String messageKey = null;
        if (toggleTimer == 0) {
            if (KeyBind.JETPACK_ENABLE.isKeyDown(player)) {
                jetpackEnabled = !jetpackEnabled;
                messageKey = "metaarmor.jetpack.flight." + (jetpackEnabled ? "enable" : "disable");
                final boolean finalEnabled = jetpackEnabled;
                stack.update(GTDataComponents.ARMOR_DATA, new GTArmor(), data1 -> data1.setEnabled(finalEnabled));
            } else if (KeyBind.ARMOR_HOVER.isKeyDown(player)) {
                hoverMode = !hoverMode;
                messageKey = "metaarmor.jetpack.hover." + (hoverMode ? "enable" : "disable");
                final boolean finalHover = hoverMode;
                stack.update(GTDataComponents.ARMOR_DATA, new GTArmor(), data1 -> data1.setHover(finalHover));
            }

            if (messageKey != null) {
                toggleTimer = 5;
                if (!world.isClientSide) player.displayClientMessage(Component.translatable(messageKey), true);
            }
        }

        if (toggleTimer > 0)
            toggleTimer--;

        final byte finalToggleTimer = toggleTimer;
        final boolean finalHover = hoverMode;
        final boolean finalEnabled = jetpackEnabled;
        stack.update(GTDataComponents.ARMOR_DATA, new GTArmor(),
                data1 -> data1.setHover(finalHover)
                        .setBurnTimer((short) burnTimer)
                        .setToggleTimer(finalToggleTimer)
                        .setEnabled(finalEnabled));

        if (currentFuel.isEmpty())
            findNewRecipe(stack);

        performFlying(player, jetpackEnabled, hoverMode, stack);
    }

    @Override
    public ArmorItem.Type getArmorType() {
        return ArmorItem.Type.CHESTPLATE;
    }

    @Override
    public int getArmorDisplay(Player player, @NotNull ItemStack armor, EquipmentSlot slot) {
        return 0;
    }

    @Override
    public void addToolComponents(@NotNull ArmorComponentItem item) {
        item.attachComponents(new Behaviour(tankCapacity));
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
                                            ArmorMaterial.Layer layer) {
        return GTCEu.id("textures/armor/liquid_fuel_jetpack.png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawHUD(@NotNull ItemStack item, GuiGraphics guiGraphics) {
        IFluidHandler tank = FluidUtil.getFluidHandler(item);
        if (tank != null) {
            if (tank.getFluidInTank(0).getAmount() == 0) return;
            String formated = String.format("%.1f",
                    (tank.getFluidInTank(0).getAmount() * 100.0F / tank.getTankCapacity(0)));
            this.HUD.newString(Component.translatable("metaarmor.hud.fuel_lvl", formated + "%"));
            GTArmor data = item.get(GTDataComponents.ARMOR_DATA);

            if (data != null) {
                Component status = data.enabled() ?
                        Component.translatable("metaarmor.hud.status.enabled") :
                        Component.translatable("metaarmor.hud.status.disabled");
                Component result = Component.translatable("metaarmor.hud.engine_enabled", status);
                this.HUD.newString(result);

                status = data.hover() ?
                        Component.translatable("metaarmor.hud.status.enabled") :
                        Component.translatable("metaarmor.hud.status.disabled");
                result = Component.translatable("metaarmor.hud.hover_mode", status);
                this.HUD.newString(result);
            }
        }
        this.HUD.draw(guiGraphics);
        this.HUD.reset();
    }

    @Override
    public int getEnergyPerUse() {
        return 1;
    }

    @Override
    public boolean canUseEnergy(ItemStack stack, int amount) {
        if (currentFuel.isEmpty()) return false;
        if (burnTimer > 0) return true;
        var ret = Optional.ofNullable(FluidUtil.getFluidHandler(stack))
                .map(h -> h.drain(Integer.MAX_VALUE, FluidAction.SIMULATE))
                .map(drained -> drained.getAmount() >= currentFuel.getAmount())
                .orElse(Boolean.FALSE);
        if (!ret) currentFuel = FluidIngredient.EMPTY;
        return ret;
    }

    @Override
    public void drainEnergy(ItemStack stack, int amount) {
        if (burnTimer == 0) {
            Optional.ofNullable(FluidUtil.getFluidHandler(stack))
                    .ifPresent(h -> h.drain(currentFuel.getAmount(), FluidAction.EXECUTE));
            burnTimer = FUELS.getInt(currentFuel);
        }
        burnTimer -= amount;
    }

    @Override
    public boolean hasEnergy(ItemStack stack) {
        return burnTimer > 0 || !currentFuel.isEmpty();
    }

    public void findNewRecipe(@NotNull ItemStack stack) {
        FluidUtil.getFluidContained(stack).ifPresentOrElse(fluid -> {
            if (!previousFuel.isEmpty() && previousFuel.test(fluid) &&
                    fluid.getAmount() >= previousFuel.getAmount()) {
                currentFuel = previousFuel;
                return;
            }

            for (var fuel : FUELS.keySet()) {
                if (fuel.test(fluid) && fluid.getAmount() >= fuel.getAmount()) {
                    previousFuel = currentFuel = fuel;
                }
            }
        }, () -> currentFuel = FluidIngredient.EMPTY);
    }

    /*
     * @Override
     * public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, @NotNull ItemStack armor,
     *
     * @NotNull DamageSource source, double damage,
     * EntityEquipmentSlot equipmentSlot) {
     * int damageLimit = (int) Math.min(Integer.MAX_VALUE, burnTimer * 1.0 / 32 * 25.0);
     * if (source.isUnblockable()) return new ISpecialArmor.ArmorProperties(0, 0.0, 0);
     * return new ISpecialArmor.ArmorProperties(0, 0, damageLimit);
     * }
     */

    public static class Behaviour implements IDurabilityBar, IItemComponent, ISubItemHandler, IAddInformation,
                                  IInteractionItem, IComponentCapability {

        public final int maxCapacity;
        private final Pair<Integer, Integer> durabilityBarColors;

        public Behaviour(int internalCapacity) {
            this.maxCapacity = internalCapacity;
            this.durabilityBarColors = GradientUtil.getGradient(0xB7AF08, 10);
        }

        @Override
        public float getDurabilityForDisplay(@NotNull ItemStack itemStack) {
            return FluidUtil.getFluidContained(itemStack)
                    .map(stack -> (float) stack.getAmount() / maxCapacity)
                    .orElse(0f);
        }

        @Nullable
        @Override
        public Pair<Integer, Integer> getDurabilityColorsForDisplay(ItemStack itemStack) {
            return durabilityBarColors;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(ItemStack itemStack, @NotNull Capability<T> cap) {
            return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(cap,
                    LazyOptional.of(() -> new FluidHandlerItemStack(itemStack, maxCapacity) {

                        @Override
                        public boolean canFillFluidType(FluidStack fluid) {
                            for (var ingredient : FUELS.keySet()) {
                                if (ingredient.test(fluid)) return true;
                            }
                            return false;
                        }
                    }));
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
                                    TooltipFlag isAdvanced) {
            GTArmor data = stack.getOrDefault(GTDataComponents.ARMOR_DATA, new GTArmor());

            Component state = data.enabled() ? Component.translatable("metaarmor.hud.status.enabled") :
                    Component.translatable("metaarmor.hud.status.disabled");
            tooltipComponents.add(Component.translatable("metaarmor.hud.engine_enabled", state));

            state = data.hover() ? Component.translatable("metaarmor.hud.status.enabled") :
                    Component.translatable("metaarmor.hud.status.disabled");
            tooltipComponents.add(Component.translatable("metaarmor.hud.hover_mode", state));
        }

        @Override
        public void attachCapabilities(RegisterCapabilitiesEvent event, Item item) {
            event.registerItem(Capabilities.FluidHandler.ITEM,
                    (stack, unused) -> new FluidHandlerItemStack(GTDataComponents.FLUID_CONTENT, stack, maxCapacity) {

                        @Override
                        public boolean canFillFluidType(FluidStack fluid) {
                            return JETPACK_FUEL_FILTER.test(fluid);
                        }
                    }, item);
        }

        @Override
        public void fillItemCategory(Item item, CreativeModeTab category, NonNullList<ItemStack> items) {
            ItemStack copy = item.getDefaultInstance();
            IFluidHandler fluidHandlerItem = FluidUtil.getFluidHandler(copy);
            if (fluidHandlerItem != null) {
                fluidHandlerItem.fill(GTMaterials.Diesel.getFluid(tankCapacity), IFluidHandler.FluidAction.EXECUTE);
                items.add(copy);
            } else {
                items.add(copy);
            }
        }
    }
}
