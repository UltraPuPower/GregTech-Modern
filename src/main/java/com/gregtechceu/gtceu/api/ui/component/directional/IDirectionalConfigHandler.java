package com.gregtechceu.gtceu.api.ui.component.directional;

import com.gregtechceu.gtceu.api.ui.component.SceneComponent;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.fancy.FancyMachineUIComponent;
import com.gregtechceu.gtceu.api.ui.util.ClickData;
import com.lowdragmc.lowdraglib.utils.BlockPosFace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IDirectionalConfigHandler {

    /**
     * Returns the buttons to display inside the side selector
     */
    UIComponent getSideSelectorWidget(SceneComponent scene, FancyMachineUIComponent machineUI);

    /**
     * Called whenever a side is selected in the side selector GUI
     */
    void onSideSelected(BlockPos pos, Direction side);

    /**
     * Determines which side of the screen the UI element should be placed on.
     */
    ScreenSide getScreenSide();

    enum ScreenSide {
        LEFT,
        RIGHT,
    }

    default void handleClick(ClickData cd, Direction direction) {
        // Do nothing by default
    }

    @OnlyIn(Dist.CLIENT)
    default void renderOverlay(SceneComponent sceneWidget, BlockPosFace blockPosFace) {
        // Do nothing by default
    }

    default void addAdditionalUIElements(FlowLayout parent) {
        // Do nothing by default
    }
}
