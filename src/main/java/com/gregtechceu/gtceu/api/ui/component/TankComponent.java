package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.Observable;

import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import java.util.List;

@Accessors(fluent = true, chain = true)
public class TankComponent extends BaseUIComponent {

    @Getter
    protected IFluidHandler handler;
    @Getter
    protected int tank;
    protected Observable<FluidStack> lastFluidInTank = Observable.of(FluidStack.EMPTY);
    protected int lastTankCapacity;
    @Setter
    protected boolean showAmount = true;

    protected TankComponent(IFluidHandler fluidHandler, int tank) {
        this.handler = fluidHandler;
        this.tank = tank;
        Observable.observeAll(this::updateListener,  this.lastFluidInTank);
    }

    public TankComponent setFluidTank(IFluidHandler handler) {
        this.handler = handler;
        this.tank = 0;
        return this;
    }

    public TankComponent setFluidTank(IFluidHandler handler, int tank) {
        this.handler = handler;
        this.tank = tank;
        return this;
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (handler != null) {
            FluidStack stack = handler.getFluidInTank(tank);
            int capacity = handler.getTankCapacity(tank);
            if (capacity != lastTankCapacity) {
                lastTankCapacity = capacity;
            }
            if (lastFluidInTank().isEmpty()) {
                lastFluidInTank.set(stack);
            }
            if (!stack.isFluidEqual(lastFluidInTank())) {
                lastFluidInTank.set(stack);
            } else if (stack.getAmount() != lastFluidInTank().getAmount()) {
                lastFluidInTank().setAmount(stack.getAmount());
            }
        }

        if (!lastFluidInTank().isEmpty()) {
            RenderSystem.disableBlend();
            if (!lastFluidInTank().isEmpty()) {
                double progress = lastFluidInTank().getAmount() * 1.0 /
                        Math.max(Math.max(lastFluidInTank().getAmount(), lastTankCapacity), 1);

                int width = width();
                int height = height();
                int x = x();
                int y = y();
                graphics.drawFluid(lastFluidInTank(), lastTankCapacity, x, y, width, height);

                graphics.pose().pushPose();
                graphics.pose().scale(0.5f, 0.5f, 1.0f);
                String s = FormattingUtil.formatBuckets(lastFluidInTank().getAmount());
                Font f = Minecraft.getInstance().font;
                graphics.drawString(f, s,
                        (int) ((x + width / 3.0f)) * 2 - f.width(s) + 21,
                        (int) ((y + (height / 3.0f) + 6) * 2), Color.WHITE.argb(), true);
                graphics.pose().popPose();
            }
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        }

        if (hovered) {
            RenderSystem.colorMask(true, true, true, false);
            graphics.drawSolidRect(x, y, width, height, Color.HOVER_GRAY.argb());
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        Player player = player();
        if(player == null) return false;
        boolean isShift = player.isShiftKeyDown();
        ItemStack currentStack = getCarried();
        var handler = FluidUtil.getFluidHandler(currentStack).resolve().orElse(null);
        if(handler == null) return false;
        int maxAttempts = isShift ? currentStack.getCount() : 1;
        FluidStack initialFluid = this.handler.getFluidInTank(tank).copy();
        if(initialFluid.getAmount() > 0) {
            boolean performedFill = false;
            ItemStack filledResult = ItemStack.EMPTY;
            for(int i = 0; i < maxAttempts; i++) {
                FluidActionResult res = FluidUtil.tryFillContainer(currentStack, this.handler, Integer.MAX_VALUE, null, false);
                if(!res.isSuccess()) break;
                ItemStack remaining = FluidUtil.tryFillContainer(currentStack, this.handler, Integer.MAX_VALUE, null, true).getResult();
                performedFill = true;

                currentStack.shrink(1);

                if(filledResult.isEmpty()) {
                    filledResult = remaining.copy();
                } else if(ItemStack.isSameItemSameTags(filledResult, remaining)) {
                    if(filledResult.getCount() < filledResult.getMaxStackSize())
                        filledResult.grow(1);
                    else
                        player.getInventory().placeItemBackInInventory(remaining);
                }
                else {
                    player.getInventory().placeItemBackInInventory(filledResult);
                    filledResult = remaining.copy();
                }
            }
            if(performedFill) {
                SoundEvent sound = initialFluid.getFluid().getFluidType().getSound(initialFluid, SoundActions.BUCKET_FILL);
                if(sound == null)
                    sound = SoundEvents.BUCKET_FILL;
                player.level().playSound(null, player.position().x, player.getEyeY(), player.position().z,
                        sound, SoundSource.BLOCKS, 1.0f, 1.0f);

                if(currentStack.isEmpty()) { // todo properly sync this to the server(or client?)
                    setCarried(filledResult);
                } else {
                    setCarried(currentStack);
                    player.getInventory().placeItemBackInInventory(filledResult);
                }
                return true;
            }
        }
        return super.onMouseDown(mouseX, mouseY, button);
    }

    public static TankComponent parse(Element element) {
        UIParsing.expectAttributes(element, "tank");
        int tank = UIParsing.parseUnsignedInt(element.getAttributeNode("tank"));

        return new TankComponent(EmptyFluidHandler.INSTANCE, tank);
    }

    @Override
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        return !this.lastFluidInTank().isEmpty() && super.shouldDrawTooltip(mouseX, mouseY);
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 16;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 16;
    }

    @NotNull
    public FluidStack lastFluidInTank() {
        return lastFluidInTank.get();
    }

    protected void updateListener() {
        if (!this.lastFluidInTank().isEmpty()) {
            this.tooltip(FluidComponent.tooltipFromFluid(this.lastFluidInTank(), Minecraft.getInstance().player, null));
        } else {
            this.tooltip((List<ClientTooltipComponent>) null);
        }
    }

}
