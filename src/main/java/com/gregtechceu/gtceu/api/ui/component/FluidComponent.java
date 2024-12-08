package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.fluids.FluidStack;

public class FluidComponent extends BaseUIComponent {

    protected final MultiBufferSource.BufferSource bufferBuilder;
    protected FluidStack stack;
    protected boolean setTooltipfromStack = false;
    protected boolean showAmount = false;

    protected FluidComponent(FluidStack stack) {
        this.bufferBuilder = Minecraft.getInstance().renderBuffers().bufferSource();
        this.stack = stack;
    }

    public FluidComponent showAmount(boolean amount) {
        this.showAmount = amount;
        return this;
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
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        var pose = graphics.pose();

        if(stack != null) {
            RenderSystem.disableBlend();
            if(!stack.isEmpty()) {
                double progress = stack.getAmount() * 1.0 /
                        Math.max(Math.max(stack.getAmount(), 16000), 1);

                graphics.drawFluid(stack, 16000, this.x, this.y, 16, 16);
            }


        }

        if(showAmount && stack != null) {
            pose.pushPose();
            pose.scale(0.5f, 0.5f, 1.0f);
            FormattedCharSequence s = Component.literal(FormattingUtil.formatBuckets(stack.getAmount())).getVisualOrderText();
            var font = Minecraft.getInstance().font;
            graphics.drawString(font, s,
                    (int)((x + (16 / 3f)) * 2 - font.width(s) + 21),
                    (int)((y + (16 / 3f) + 6) * 2), Color.WHITE.argb());
            pose.popPose();
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
