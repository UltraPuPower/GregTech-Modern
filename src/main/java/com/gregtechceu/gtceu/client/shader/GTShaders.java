package com.gregtechceu.gtceu.client.shader;

import com.google.gson.JsonSyntaxException;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.client.renderer.GTRenderTypes;
import com.gregtechceu.gtceu.client.shader.post.BloomType;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class GTShaders {

    public static final Minecraft mc = Minecraft.getInstance();

    public static PostChain BLOOM_CHAIN;
    public static BloomType BLOOM_TYPE;
    public static RenderTarget BLOOM_TARGET;

    public static VertexBuffer BLOOM_BUFFER = new VertexBuffer(VertexBuffer.Usage.STATIC);
    public static BufferBuilder BLOOM_BUFFER_BUILDER = new BufferBuilder(GTRenderTypes.getBloom().bufferSize());
    public static BufferBuilder.RenderedBuffer RENDERED_BLOOM_BUFFER = null;

    public static void onRegisterShaders(RegisterShadersEvent event) {
        if (!allowedShader()) {
            return;
        }

        initPostShaders();
    }

    private static void initPostShaders() {
        if (BLOOM_CHAIN != null) {
            BLOOM_CHAIN.close();
        }

        ResourceLocation id;

        switch (ConfigHolder.INSTANCE.client.shader.bloomStyle) {
            case 0 -> {
                id = GTCEu.id("shaders/post/bloom_gaussian.json");
                BLOOM_TYPE = BloomType.GAUSSIAN;
            }
            case 1 -> {
                id = GTCEu.id("shaders/post/bloom_unity.json");
                BLOOM_TYPE = BloomType.UNITY;
            }
            case 2 -> {
                id = GTCEu.id("shaders/post/bloom_unreal.json");
                BLOOM_TYPE = BloomType.UNREAL;
            }
            default -> {
                GTCEu.LOGGER.error("Invalid bloom style {}", ConfigHolder.INSTANCE.client.shader.bloomStyle);
                BLOOM_TYPE = BloomType.DISABLED;
                BLOOM_CHAIN = null;
                BLOOM_TARGET = null;
                return;
            }
        }

        try {
            BLOOM_CHAIN = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), id);
            BLOOM_CHAIN.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            BLOOM_TARGET = BLOOM_CHAIN.getTempTarget("final");
        } catch (IOException ioexception) {
            GTCEu.LOGGER.error("Failed to load shader: {}", id, ioexception);
            BLOOM_CHAIN = null;
            BLOOM_TARGET = null;
        } catch (JsonSyntaxException jsonsyntaxexception) {
            GTCEu.LOGGER.error("Failed to parse shader: {}", id, jsonsyntaxexception);
            BLOOM_CHAIN = null;
            BLOOM_TARGET = null;
        }
    }

    public static boolean allowedShader() {
        return ConfigHolder.INSTANCE.client.shader.useShader && !GTCEu.isIrisOculusLoaded();
    }

    public static float getITime(float pPartialTicks) {
        if (mc.level == null) {
            return System.currentTimeMillis() % 1200000 / 1000f;
        } else {
            return ((mc.level.getGameTime() % 24000) + pPartialTicks) / 20f;
        }
    }
}