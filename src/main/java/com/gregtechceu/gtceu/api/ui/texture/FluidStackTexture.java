package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelParsingException;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.common.commands.arguments.FluidParser;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.w3c.dom.Element;

import java.util.Map;

@Accessors(fluent = true, chain = true)
public class FluidStackTexture extends TransformTexture {

    @Getter
    @Setter
    protected FluidStack stack;

    public FluidStackTexture(FluidStack stack) {
        this.stack = stack;
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width,
                                float height) {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, -200);
        graphics.drawFluid(stack, stack.getAmount(), x, y, width, height);
        graphics.pose().popPose();
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "stack", $ -> $.getTextContent().strip(), stackString -> {
            try {
                var result = FluidParser.parseForFluid(BuiltInRegistries.FLUID.asLookup(),
                        new StringReader(stackString));

                var stack = new FluidStack(result.fluid().value(), 1000);
                stack.setTag(result.nbt());

                this.stack(stack);
            } catch (CommandSyntaxException cse) {
                throw new UIModelParsingException("Invalid fluid stack", cse);
            }
        });
    }
}
