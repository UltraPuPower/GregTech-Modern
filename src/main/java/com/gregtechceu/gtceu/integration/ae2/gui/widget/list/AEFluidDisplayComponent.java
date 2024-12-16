package com.gregtechceu.gtceu.integration.ae2.gui.widget.list;

import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.client.TooltipsHandler;
import com.gregtechceu.gtceu.utils.GTMath;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.side.fluid.forge.FluidHelperImpl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEConfigSlotComponent.drawSelectionOverlay;
import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawText;

/**
 * @author GlodBlock
 * @ Display a certain {@link FluidStack} element.
 * @date 2023/4/19-0:30
 */
public class AEFluidDisplayComponent extends BaseUIComponent {

    private final AEListGridComponent gridWidget;
    private final int index;

    public AEFluidDisplayComponent(AEListGridComponent gridWidget, int index) {
        this.gridWidget = gridWidget;
        this.index = index;
        this.sizing(Sizing.fixed(18));
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        GenericStack fluid = this.gridWidget.getAt(this.index);
        if (fluid != null) {
            FluidStack fluidStack = fluid.what() instanceof AEFluidKey key ?
                    new FluidStack(key.getFluid(), GTMath.saturatedCast(fluid.amount()), key.getTag()) :
                    FluidStack.EMPTY;
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(fluidStack.getDisplayName());
            tooltips.add(Component.literal(String.format("%,d mB", fluid.amount())));
            TooltipsHandler.appendFluidTooltips(fluidStack, tooltips::add,
                    TooltipFlag.NORMAL);
            this.tooltip(tooltips);
        }
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        GenericStack fluid = this.gridWidget.getAt(this.index);
        GuiTextures.FLUID_SLOT.draw(graphics, mouseX, mouseY, x(), y(), 18, 18);
        GuiTextures.NUMBER_BACKGROUND.draw(graphics, mouseX, mouseY, x() + 18, y(), 140, 18);
        int stackX = x() + 1;
        int stackY = y() + 1;
        if (fluid != null) {
            FluidStack fluidStack = fluid.what() instanceof AEFluidKey key ?
                    new FluidStack(key.getFluid(), GTMath.saturatedCast(fluid.amount()), key.getTag()) :
                    FluidStack.EMPTY;
            DrawerHelper.drawFluidForGui(graphics, FluidHelperImpl.toFluidStack(fluidStack), fluid.amount(), stackX,
                    stackY, 16, 16);
            String amountStr = String.format("x%,d", fluid.amount());
            drawText(graphics, amountStr, stackX + 20, stackY + 5, 1, 0xFFFFFFFF);
        }
        if (isMouseOverElement(mouseX, mouseY)) {
            drawSelectionOverlay(graphics, stackX, stackY, 16, 16);
        }
    }
}
