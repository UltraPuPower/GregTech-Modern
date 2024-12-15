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
import com.gregtechceu.gtceu.api.ui.component.directional.IDirectionalConfigHandler;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputFluid;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputItem;
import com.gregtechceu.gtceu.api.ui.util.ClickData;

import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
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
public class AutoOutputItemConfigHandler implements IDirectionalConfigHandler {

    private static final UITexture TEXTURE_OFF = UITextures.group(
            GuiTextures.VANILLA_BUTTON,
            GuiTextures.IO_CONFIG_ITEM_MODES_BUTTON.getSubTexture(0, 0, 1, 1 / 3f));
    private static final UITexture TEXTURE_OUTPUT = UITextures.group(
            GuiTextures.VANILLA_BUTTON,
            GuiTextures.IO_CONFIG_ITEM_MODES_BUTTON.getSubTexture(0, 1 / 3f, 1, 1 / 3f));
    private static final UITexture TEXTURE_AUTO = UITextures.group(
            GuiTextures.VANILLA_BUTTON,
            GuiTextures.IO_CONFIG_ITEM_MODES_BUTTON.getSubTexture(0, 2 / 3f, 1, 1 / 3f));

    private final IAutoOutputItem machine;
    private Direction side;
    private ButtonComponent ioModeButton;

    public AutoOutputItemConfigHandler(IAutoOutputItem machine) {
        this.machine = machine;
    }

    @Override
    public UIComponent getSideSelectorWidget(SceneComponent scene, FancyMachineUIComponent machineUI) {
        FlowLayout group = UIContainers.horizontalFlow(Sizing.fixed((18 * 2) + 1), Sizing.fixed(18));

        group.child(ioModeButton = new ButtonComponent(Component.empty(), this::onIOModePressed) {

            @Override
            public void update(float delta, int mouseX, int mouseY) {
                super.update(delta, mouseX, mouseY);

                if (machine.getOutputFacingItems() == side) {
                    if (machine.isAutoOutputItems()) {
                        renderer(Renderer.texture(TEXTURE_AUTO));
                    } else {
                        renderer(Renderer.texture(TEXTURE_OUTPUT));
                    }
                } else {
                    renderer(Renderer.texture(TEXTURE_OFF));
                }
            }
        });
        ioModeButton.sizing(Sizing.fill());
        ioModeButton.positioning(Positioning.relative(100, 0));

        group.child(UIComponents.toggleButton(GuiTextures.BUTTON_ITEM_OUTPUT,
                machine::isAllowInputFromOutputSideItems, machine::setAllowInputFromOutputSideItems)
                .shouldUseBaseBackground().setTooltipText("gtceu.gui.item_auto_output.allow_input")
                .positioning(Positioning.absolute(19, 0))
                .sizing(Sizing.fill()));

        return group;
    }

    private void onIOModePressed(com.gregtechceu.gtceu.api.ui.util.ClickData btn) {
        if (this.side == null)
            return;

        if (machine.getOutputFacingItems() == this.side) {
            machine.setAutoOutputItems(!machine.isAutoOutputItems());
        } else {
            machine.setAutoOutputItems(false);
            machine.setOutputFacingItems(this.side);
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
        if (!canHandleClick(cd) || !machine.hasAutoOutputItem())
            return;

        if (machine.getOutputFacingItems() != side) {
            machine.setOutputFacingItems(side);
            machine.setAutoOutputItems(false);
        } else {
            machine.setAutoOutputItems(!machine.isAutoOutputItems());
        }
    }

    @SuppressWarnings("RedundantIfStatement") // Cleaner code this way
    private boolean canHandleClick(ClickData cd) {
        if (cd.button == 0)
            return true;

        if (!(machine instanceof IAutoOutputFluid) && cd.button == 1)
            return true;

        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderOverlay(SceneComponent sceneWidget, BlockPosFace blockPosFace) {
        if (machine.getOutputFacingItems() != blockPosFace.facing)
            return;

        sceneWidget.drawFacingBorder(new PoseStack(), blockPosFace,
                machine.isAutoOutputItems() ? 0xffff6e0f : 0x8fff6e0f, 1);
    }

    @Override
    public void addAdditionalUIElements(FlowLayout parent) {
        LabelComponent text = new LabelComponent(Component.translatable("gtceu.gui.auto_output.name")) {

            // TODO implement
            //@Override
            //public boolean isVisible() {
            //    return machine.isAutoOutputItems() && machine.getOutputFacingItems() != null;
            //}
        };

        text.color(Color.ofArgb(0xff00b4ff));
        parent.child(text);
    }
}
