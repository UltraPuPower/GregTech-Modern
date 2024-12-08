package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.api.ui.util.pond.UISlotExtension;
import com.gregtechceu.gtceu.core.mixins.ui.accessor.SlotAccessor;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Element;

public class TankComponent extends BaseUIComponent {

    @Getter
    protected final IFluidHandler handler;
    @Getter @Setter
    protected String handlerName;
    protected final int tank;

    protected TankComponent(IFluidHandler fluidHandler, int tank) {
        this.handler = fluidHandler;
        this.tank = tank;
    }

    @Override
    public void draw(UIGuiGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        int[] scissor = new int[4];
        GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissor);

        ((UISlotExtension) this.handler).gtceu$setScissorArea(PositionedRectangle.of(
                scissor[0], scissor[1], scissor[2], scissor[3]));
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

    @Override
    public TankComponent x(int x) {
        super.x(x);
        ((SlotAccessor) this.handler).gtceu$setX(x);
        return this;
    }

    @Override
    public TankComponent y(int y) {
        super.y(y);
        ((SlotAccessor) this.handler).gtceu$setY(y);
        return this;
    }

}
