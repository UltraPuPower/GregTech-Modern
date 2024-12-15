package com.gregtechceu.gtceu.api.ui.component.directional.handlers;

import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.ui.component.*;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Color;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.fancy.FancyMachineUIComponent;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.api.ui.component.directional.IDirectionalConfigHandler;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputFluid;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputItem;

import com.gregtechceu.gtceu.api.ui.util.ClickData;
import com.lowdragmc.lowdraglib.utils.BlockPosFace;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AutoOutputFluidConfigHandler implements IDirectionalConfigHandler {

    private static final UITexture TEXTURE_OFF = UITextures.group(
            GuiTextures.VANILLA_BUTTON,
            GuiTextures.IO_CONFIG_FLUID_MODES_BUTTON.getSubTexture(0, 0, 1, 1 / 3f));
    private static final UITexture TEXTURE_OUTPUT = UITextures.group(
            GuiTextures.VANILLA_BUTTON,
            GuiTextures.IO_CONFIG_FLUID_MODES_BUTTON.getSubTexture(0, 1 / 3f, 1, 1 / 3f));
    private static final UITexture TEXTURE_AUTO = UITextures.group(
            GuiTextures.VANILLA_BUTTON,
            GuiTextures.IO_CONFIG_FLUID_MODES_BUTTON.getSubTexture(0, 2 / 3f, 1, 1 / 3f));

    private final IAutoOutputFluid machine;
    private Direction side;
    private ButtonComponent ioModeButton;

    public AutoOutputFluidConfigHandler(IAutoOutputFluid machine) {
        this.machine = machine;
    }

    @Override
    public UIComponent getSideSelectorWidget(SceneComponent scene, FancyMachineUIComponent machineUI) {
        FlowLayout group = UIContainers.horizontalFlow(Sizing.content(1), Sizing.content());

        group.child(ioModeButton = new ButtonComponent(Component.empty(), this::onIOModePressed) {

            @Override
            public void update(float delta, int mouseX, int mouseY) {
                super.update(delta, mouseX, mouseY);
                if (machine.getOutputFacingFluids() == side) {
                    if (machine.isAutoOutputFluids()) {
                        renderer(Renderer.texture(TEXTURE_AUTO));
                    } else {
                        renderer(Renderer.texture(TEXTURE_OUTPUT));
                    }
                } else {
                    renderer(Renderer.texture(TEXTURE_OFF));
                }
            }
        });
        ioModeButton.sizing(Sizing.fixed(18));
        ioModeButton.positioning(Positioning.relative(100, 0));

        group.child(UIComponents.toggleButton(GuiTextures.BUTTON_FLUID_OUTPUT,
                        machine::isAllowInputFromOutputSideFluids, machine::setAllowInputFromOutputSideFluids)
                .shouldUseBaseBackground().setTooltipText("gtceu.gui.fluid_auto_output.allow_input")
                .positioning(Positioning.layout())
                .sizing(Sizing.fixed(18)));

        return group;
    }

    private void onIOModePressed(ClickData button) {
        if (this.side == null)
            return;

        if (machine.getOutputFacingFluids() == this.side) {
            machine.setAutoOutputFluids(!machine.isAutoOutputFluids());
        } else {
            machine.setAutoOutputFluids(false);
            machine.setOutputFacingFluids(this.side);
        }
    }

    @Override
    public void onSideSelected(BlockPos pos, Direction side) {
        this.side = side;
    }

    @Override
    public ScreenSide getScreenSide() {
        return ScreenSide.LEFT;
    }

    @Override
    public void handleClick(ClickData cd, Direction direction) {
        if (!canHandleClick(cd) || !machine.hasAutoOutputFluid())
            return;

        if (machine.getOutputFacingFluids() != side) {
            machine.setOutputFacingFluids(side);
            machine.setAutoOutputFluids(false);
        } else {
            machine.setAutoOutputFluids(!machine.isAutoOutputFluids());
        }
    }

    @SuppressWarnings("RedundantIfStatement") // Cleaner code this way
    private boolean canHandleClick(ClickData cd) {
        if (cd.button == 1)
            return true;

        if (!(machine instanceof IAutoOutputItem) && cd.button == 0)
            return true;

        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderOverlay(SceneComponent sceneWidget, BlockPosFace blockPosFace) {
        if (machine.getOutputFacingFluids() != blockPosFace.facing)
            return;

        sceneWidget.drawFacingBorder(new PoseStack(), blockPosFace,
                machine.isAutoOutputFluids() ? 0xff00b4ff : 0x8f00b4ff, 2);
    }

    @Override
    public void addAdditionalUIElements(FlowLayout parent) {
        LabelComponent text = new LabelComponent(Component.translatable("gtceu.gui.auto_output.name")) {

            //@Override
            //public boolean isVisible() {
            //    return machine.isAutoOutputFluids() && machine.getOutputFacingFluids() != null;
            //}
        };
        text.positioning(Positioning.absolute(parent.width() - 4 - text.width(), 4));

        text.color(Color.ofArgb(0xff00b4ff));
        parent.child(text);
    }
}
