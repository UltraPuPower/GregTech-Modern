package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.pond.UISlotExtension;
import com.gregtechceu.gtceu.client.TooltipsHandler;
import com.gregtechceu.gtceu.client.ui.screens.SyncedProperty;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.SlotAccessor;

import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TankComponent extends BaseUIComponent {

    @Getter
    protected IFluidHandler handler;
    @Getter
    @Setter
    protected String handlerName;
    protected int tank;
    protected FluidStack lastFluidInTank;
    protected int lastTankCapacity;
    protected boolean showAmount = true;

    protected TankComponent(IFluidHandler fluidHandler, int tank) {
        this.handler = fluidHandler;
        this.tank = tank;
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

    public TankComponent showAmount(boolean show) {
        showAmount = show;
        return this;
    }

    public List<Component> getTooltips() {
        List<Component> tooltips = new ArrayList<>();
        var stack = lastFluidInTank;
        if(stack != null && !stack.isEmpty()) {
            tooltips.add(stack.getDisplayName());
            if (true && showAmount) { // todo phantom stacks
                tooltips.add(
                        Component.translatable("ldlib.fluid.amount", stack.getAmount(), lastTankCapacity)
                                .append(" mB"));
            }
            if (ChemicalHelper.getMaterial(stack.getFluid()) != null) {
                TooltipsHandler.appendFluidTooltips(stack.getFluid(), stack.getAmount(), tooltips::add, null);
            } else {
                tooltips.add(Component.translatable("ldlib.fluid.temperature",
                        stack.getFluid().getFluidType().getTemperature(stack)));
                tooltips.add(Component.translatable(stack.getFluid().getFluidType().isLighterThanAir() ?
                        "ldlib.fluid.state_gas" : "ldlib.fluid.state_liquid"));
            }
        } else {
            tooltips.add(Component.translatable("ldlib.fluid.empty"));
            if (true && showAmount) { // todo phantom stack
                tooltips.add(Component.translatable("ldlib.fluid.amount", 0, lastTankCapacity).append(" mB"));
            }
        }

        return tooltips;
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if(handler != null) {
            FluidStack stack = handler.getFluidInTank(tank);
            int capacity = handler.getTankCapacity(tank);
            if(capacity != lastTankCapacity) {
                lastTankCapacity = capacity;
            }
            if(lastFluidInTank == null) {
                lastFluidInTank = stack;
            }
            if(!stack.isFluidEqual(lastFluidInTank)) {
                lastFluidInTank = stack;
            } else if( stack.getAmount() != lastFluidInTank.getAmount()) {
                lastFluidInTank.setAmount(stack.getAmount());
            }
        }

        if(lastFluidInTank != null) {
            RenderSystem.disableBlend();
            if(!lastFluidInTank.isEmpty()) {
                double progress = lastFluidInTank.getAmount() * 1.0 /
                        Math.max(Math.max(lastFluidInTank.getAmount(), lastTankCapacity), 1);

                int width = width();
                int height = height();
                int x = x();
                int y = y();
                graphics.drawFluid(lastFluidInTank, lastTankCapacity, x, y, width, height);
            }

            if(!lastFluidInTank.isEmpty()) {
                graphics.pose().pushPose();
                graphics.pose().scale(0.5f, 0.5f, 1.0f);
                String s = FormattingUtil.formatBuckets(lastFluidInTank.getAmount());
                Font f = Minecraft.getInstance().font;
                graphics.drawString(f, s,
                        (int) ((x + width / 3.f)) * 2 - f.width(s) + 21,
                        (int) ((y + (height / 3.0f) + 6) * 2), Color.WHITE.argb(), true);
                graphics.pose().popPose();
            }
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        }

        if(hovered) {
            RenderSystem.colorMask(true, true, true, false);
            graphics.drawSolidRect(x, y, width, height, Color.HOVER_GRAY.argb());
            RenderSystem.colorMask(true, true, true, true);
        }

        if(hovered) {
            RenderSystem.disableScissor();
            RenderSystem.disableDepthTest();
            graphics.pose().pushPose();
            graphics.pose().translate(0f,0f, 200f);
            graphics.renderTooltip(Minecraft.getInstance().font, getTooltips(), Optional.empty(), ItemStack.EMPTY, mouseX, mouseY);
            RenderSystem.setShaderColor(1,1,1,1);
            graphics.pose().popPose();
            graphics.renderTooltip(Minecraft.getInstance().font, getTooltips(), Optional.empty(), ItemStack.EMPTY, mouseX, mouseY);
        }
    }

    public static TankComponent parse(Element element) {
        UIParsing.expectAttributes(element, "tank");
        UIParsing.expectAttributes(element, "name");
        int tank = UIParsing.parseUnsignedInt(element.getAttributeNode("tank"));
        String name = element.getAttribute("name");

        TankComponent component = new TankComponent(EmptyFluidHandler.INSTANCE, tank);
        component.setHandlerName(name);
        return component;
    }

    @Override
    public void drawTooltip(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        if (!this.handler.getFluidInTank(tank).isEmpty()) {
            super.drawTooltip(graphics, mouseX, mouseY, partialTicks, delta);
        }
    }

    @Override
    public boolean shouldDrawTooltip(double mouseX, double mouseY) {
        return super.shouldDrawTooltip(mouseX, mouseY);
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 16;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 16;
    }
}
