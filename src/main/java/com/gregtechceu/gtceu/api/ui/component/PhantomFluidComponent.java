package com.gregtechceu.gtceu.api.ui.component;

import com.google.common.collect.Lists;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.ingredient.IGhostIngredientTarget;
import com.lowdragmc.lowdraglib.gui.ingredient.Target;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.lowdragmc.lowdraglib.side.fluid.forge.FluidHelperImpl;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.stack.EmiStack;
import lombok.Getter;
import lombok.experimental.Accessors;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Accessors(fluent = true, chain = true)
public class PhantomFluidComponent extends TankComponent implements IGhostIngredientTarget {

    private final Supplier<FluidStack> phantomFluidGetter;
    private final Consumer<FluidStack> phantomFluidSetter;

    @Nullable
    @Getter
    protected FluidStack lastPhantomStack;

    public PhantomFluidComponent(@Nullable IFluidHandler fluidTank, int tank,
                                 Supplier<FluidStack> phantomFluidGetter, Consumer<FluidStack> phantomFluidSetter) {
        super(fluidTank, tank);
        this.canInsert = false;
        this.canExtract = false;
        this.phantomFluidGetter = phantomFluidGetter;
        this.phantomFluidSetter = phantomFluidSetter;
    }

    public PhantomFluidComponent canExtract(boolean v) {
        // you cant modify it
        return this;
    }

    public PhantomFluidComponent canInsert(boolean v) {
        // you can't modify it
        return this;
    }

    protected void lastPhantomStack(FluidStack fluid) {
        if (fluid != null) {
            this.lastPhantomStack = fluid.copy();
            this.lastPhantomStack.setAmount(1);
        } else {
            this.lastPhantomStack = null;
        }
    }

    public static FluidStack drainFrom(Object ingredient) {
        if (ingredient instanceof Ingredient ing) {
            var items = ing.getItems();
            if (items.length > 0) {
                ingredient = items[0];
            }
        }
        if (ingredient instanceof ItemStack itemStack) {
            return FluidUtil.getFluidHandler(itemStack)
                    .map(h -> h.drain(Integer.MAX_VALUE, FluidAction.SIMULATE))
                    .orElse(FluidStack.EMPTY);
        }
        return FluidStack.EMPTY;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Target> getPhantomTargets(Object ingredient) {
        if (LDLib.isReiLoaded() && ingredient instanceof dev.architectury.fluid.FluidStack fluidStack) {
            ingredient = new FluidStack(fluidStack.getFluid(), (int) fluidStack.getAmount(), fluidStack.getTag());
        } else if (LDLib.isEmiLoaded() && ingredient instanceof EmiStack emiStack) {
            var key = emiStack.getKey();
            if (key instanceof Fluid f) {
                int amount = emiStack.getAmount() == 0 ? 1000 : (int) emiStack.getAmount();
                ingredient = new FluidStack(f, amount, emiStack.getNbt());
            } else if (key instanceof Item i) {
                ingredient = new ItemStack(i, (int) emiStack.getAmount());
                ((ItemStack) ingredient).setTag(emiStack.getNbt());
            } else {
                ingredient = null;
            }
        } else if (LDLib.isJeiLoaded() && ingredient instanceof ITypedIngredient<?> jeiStack) {
            ingredient = jeiStack.getIngredient();
        }

        if (!(ingredient instanceof FluidStack) && drainFrom(ingredient).isEmpty()) {
            return Collections.emptyList();
        }

        Rect2i rectangle = new Rect2i(x(), y(), width(), height());
        return Lists.newArrayList(new Target() {

            @Nonnull
            @Override
            public Rect2i getArea() {
                return rectangle;
            }

            @Override
            public void accept(@Nonnull Object ingredient) {
                if (LDLib.isReiLoaded() && ingredient instanceof dev.architectury.fluid.FluidStack fluidStack) {
                    ingredient = new FluidStack(fluidStack.getFluid(),
                            (int) fluidStack.getAmount(),
                            fluidStack.getTag());
                } else if (LDLib.isEmiLoaded() && ingredient instanceof EmiStack emiStack) {
                    var key = emiStack.getKey();
                    if (key instanceof Fluid f) {
                        int amount = emiStack.getAmount() == 0 ? 1000 : (int) emiStack.getAmount();
                        ingredient = new FluidStack(f, amount, emiStack.getNbt());
                    } else if (key instanceof Item i) {
                        ingredient = new ItemStack(i, (int) emiStack.getAmount());
                        ((ItemStack) ingredient).setTag(emiStack.getNbt());
                    } else {
                        ingredient = null;
                    }
                }

                FluidStack ingredientStack;
                if (ingredient instanceof FluidStack fluidStack) ingredientStack = fluidStack;
                else ingredientStack = drainFrom(ingredient);

                if (!ingredientStack.isEmpty()) {
                    sendMessage(2, ingredientStack::writeToPacket);
                }

                if (phantomFluidSetter != null) {
                    phantomFluidSetter.accept(ingredientStack);
                }
            }
        });
    }

    @Override
    public void receiveMessage(int id, FriendlyByteBuf buf) {
        if (id == 1) {
            handlePhantomClick();
        } else if (id == 2) {
            if (phantomFluidSetter != null) {
                phantomFluidSetter.accept(FluidStack.readFromPacket(buf));
            }
        } else if (id == 4) {
            phantomFluidSetter.accept(FluidStack.EMPTY);
        } else if (id == 5) {
            phantomFluidSetter.accept(FluidStack.readFromPacket(buf));
        }
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        FluidStack stack = phantomFluidGetter.get();
        if (stack == null || stack.isEmpty()) {
            if (lastPhantomStack != null) {
                lastPhantomStack(null);
                sendMessage(4, buf -> {});
            }
        } else if (lastPhantomStack == null || !stack.isFluidEqual(lastPhantomStack)) {
            lastPhantomStack(stack);
            sendMessage(5, stack::writeToPacket);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY)) {
            handlePhantomClick();
            return true;
        }
        return false;
    }

    private void handlePhantomClick() {
        ItemStack itemStack = getCarried();
        FluidStack fluid = FluidUtil.getFluidContained(itemStack)
                .map(f -> new FluidStack(f, FluidType.BUCKET_VOLUME))
                .orElse(FluidStack.EMPTY);
        if (phantomFluidSetter != null) phantomFluidSetter.accept(fluid);
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.lastFluidInTank != null) {
            super.draw(graphics, mouseX, mouseY, partialTicks, delta);
            return;
        }
        FluidStack stack = phantomFluidGetter.get();
        if (stack != null && !stack.isEmpty()) {
            RenderSystem.disableBlend();

            double progress = stack.getAmount() * 1.0 / Math.max(Math.max(stack.getAmount(), lastTankCapacity), 1);
            float drawnU = (float) fillDirection.getDrawnU(progress);
            float drawnV = (float) fillDirection.getDrawnV(progress);
            float drawnWidth = (float) fillDirection.getDrawnWidth(progress);
            float drawnHeight = (float) fillDirection.getDrawnHeight(progress);
            int width = width() - 2;
            int height = height() - 2;
            int x = x() + 1;
            int y = y() + 1;
            DrawerHelper.drawFluidForGui(graphics, FluidHelperImpl.toFluidStack(stack), stack.getAmount(),
                    (int) (x + drawnU * width), (int) (y + drawnV * height), ((int) (width * drawnWidth)),
                    ((int) (height * drawnHeight)));
            if (showAmount) {
                graphics.pose().pushPose();
                graphics.pose().scale(0.5F, 0.5F, 1);
                String s = TextFormattingUtil.formatLongToCompactStringBuckets(stack.getAmount(), 3) + "B";
                Font fontRenderer = Minecraft.getInstance().font;
                graphics.drawString(fontRenderer, s,
                        (int) ((x() + (width() / 3f)) * 2 - fontRenderer.width(s) + 21),
                        (int) ((y() + (height() / 3f) + 6) * 2), 0xFFFFFF, true);
                graphics.pose().popPose();
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

}
