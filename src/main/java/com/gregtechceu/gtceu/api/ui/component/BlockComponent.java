package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModelParsingException;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.w3c.dom.Element;

public class BlockComponent extends BaseUIComponent {

    private final Minecraft mc = Minecraft.getInstance();

    private final BlockState state;
    private final @Nullable BlockEntity entity;

    protected BlockComponent(BlockState state, @Nullable BlockEntity entity) {
        this.state = state;
        this.entity = entity;
    }

    @Override
    @SuppressWarnings("NonAsciiCharacters")
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        graphics.pose().pushPose();

        graphics.pose().translate(x + this.width / 2f, y + this.height / 2f, 100);
        graphics.pose().scale(40 * this.width / 64f, -40 * this.height / 64f, 40);

        graphics.pose().mulPose(Axis.XP.rotationDegrees(30));
        graphics.pose().mulPose(Axis.YP.rotationDegrees(45 + 180));

        graphics.pose().translate(-.5, -.5, -.5);

        RenderSystem.runAsFancy(() -> {
            final var vertexConsumers = mc.renderBuffers().bufferSource();
            if (this.state.getRenderShape() != RenderShape.ENTITYBLOCK_ANIMATED) {
                this.mc.getBlockRenderer().renderSingleBlock(
                        this.state, graphics.pose(), vertexConsumers,
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            }

            if (this.entity != null) {
                var entityRender = this.mc.getBlockEntityRenderDispatcher().getRenderer(this.entity);
                if (entityRender != null) {
                    entityRender.render(entity, partialTicks, graphics.pose(), vertexConsumers,
                            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                }
            }

            RenderSystem.setShaderLights(new Vector3f(-1.5f, -.5f, 0), new Vector3f(0, -1, 0));
            vertexConsumers.endBatch();
            Lighting.setupFor3DItems();
        });

        graphics.pose().popPose();
    }

    protected static void prepareBlockEntity(BlockState state, BlockEntity blockEntity, @Nullable CompoundTag nbt) {
        if (blockEntity == null) return;

        blockEntity.setBlockState(state);
        blockEntity.setLevel(Minecraft.getInstance().level);

        if (nbt == null) return;

        final var nbtCopy = nbt.copy();

        nbtCopy.putInt("x", 0);
        nbtCopy.putInt("y", 0);
        nbtCopy.putInt("z", 0);

        blockEntity.load(nbtCopy);
    }

    public static BlockComponent parse(Element element) {
        UIParsing.expectAttributes(element, "state");

        try {
            var result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(),
                    element.getAttribute("state"), true);
            return UIComponents.block(result.blockState(), result.nbt());
        } catch (CommandSyntaxException cse) {
            throw new UIModelParsingException("Invalid block state", cse);
        }
    }
}
