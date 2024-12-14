package com.gregtechceu.gtceu.api.ui.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import org.lwjgl.glfw.GLFW;

public class ClickData {
    public final int button;
    public final boolean isShiftClick;
    public final boolean isCtrlClick;
    public final boolean isClientSide;

    private ClickData(int button, boolean isShiftClick, boolean isCtrlClick, boolean isClientSide) {
        this.button = button;
        this.isShiftClick = isShiftClick;
        this.isCtrlClick = isCtrlClick;
        this.isClientSide = isClientSide;
    }

    public ClickData(int button) {
        long id = Minecraft.getInstance().getWindow().getWindow();
        this.button = button;
        this.isShiftClick = InputConstants.isKeyDown(id, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(id, GLFW.GLFW_KEY_RIGHT_SHIFT);
        this.isCtrlClick = InputConstants.isKeyDown(id, GLFW.GLFW_KEY_LEFT_CONTROL) || InputConstants.isKeyDown(id, GLFW.GLFW_KEY_RIGHT_CONTROL);
        this.isClientSide = true;
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeByte(button);
        buf.writeBoolean(isShiftClick);
        buf.writeBoolean(isCtrlClick);
    }

    public static ClickData readFromBuf(FriendlyByteBuf buf) {
        int button = buf.readByte();
        boolean shiftClick = buf.readBoolean();
        boolean ctrlClick = buf.readBoolean();
        return new ClickData(button, shiftClick, ctrlClick, false);
    }
}
