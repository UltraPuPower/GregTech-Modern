package com.gregtechceu.gtceu.api.ui.util;

import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.core.Size;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NinePatchTexture {

    private final ResourceLocation texture;
    private final int u, v;
    private final Size cornerPatchSize;
    private final Size centerPatchSize;
    private final Size textureSize;
    private final boolean repeat;

    public NinePatchTexture(ResourceLocation texture, int u, int v, Size cornerPatchSize, Size centerPatchSize,
                            Size textureSize, boolean repeat) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.textureSize = textureSize;
        this.cornerPatchSize = cornerPatchSize;
        this.centerPatchSize = centerPatchSize;
        this.repeat = repeat;
    }

    public NinePatchTexture(ResourceLocation texture, int u, int v, Size patchSize, Size textureSize, boolean repeat) {
        this(texture, u, v, patchSize, patchSize, textureSize, repeat);
    }

    public NinePatchTexture(ResourceLocation texture, Size patchSize, Size textureSize, boolean repeat) {
        this(texture, 0, 0, patchSize, textureSize, repeat);
    }

    public void draw(UIGuiGraphics context, PositionedRectangle rectangle) {
        this.draw(context, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
    }

    public void draw(UIGuiGraphics context, int x, int y, int width, int height) {
        int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
        int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();

        context.blit(this.texture, x, y, this.u, this.v, this.cornerPatchSize.width(), this.cornerPatchSize.height(),
                this.textureSize.width(), this.textureSize.height());
        context.blit(this.texture, x + width - this.cornerPatchSize.width(), y, this.u + rightEdge, this.v,
                this.cornerPatchSize.width(), this.cornerPatchSize.height(), this.textureSize.width(),
                this.textureSize.height());
        context.blit(this.texture, x, y + height - this.cornerPatchSize.height(), this.u, this.v + bottomEdge,
                this.cornerPatchSize.width(), this.cornerPatchSize.height(), this.textureSize.width(),
                this.textureSize.height());
        context.blit(this.texture, x + width - this.cornerPatchSize.width(), y + height - this.cornerPatchSize.height(),
                this.u + rightEdge, this.v + bottomEdge, this.cornerPatchSize.width(), this.cornerPatchSize.height(),
                this.textureSize.width(), this.textureSize.height());

        if (this.repeat) {
            this.drawRepeated(context, x, y, width, height);
        } else {
            this.drawStretched(context, x, y, width, height);
        }
    }

    protected void drawStretched(UIGuiGraphics context, int x, int y, int width, int height) {
        int doubleCornerHeight = this.cornerPatchSize.height() * 2;
        int doubleCornerWidth = this.cornerPatchSize.width() * 2;

        int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
        int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();

        if (width > doubleCornerWidth && height > doubleCornerHeight) {
            context.blit(this.texture, x + this.cornerPatchSize.width(), y + this.cornerPatchSize.height(),
                    width - doubleCornerWidth, height - doubleCornerHeight, this.u + this.cornerPatchSize.width(),
                    this.v + this.cornerPatchSize.height(), this.centerPatchSize.width(), this.centerPatchSize.height(),
                    this.textureSize.width(), this.textureSize.height());
        }

        if (width > doubleCornerWidth) {
            context.blit(this.texture, x + this.cornerPatchSize.width(), y, width - doubleCornerWidth,
                    this.cornerPatchSize.height(), this.u + this.cornerPatchSize.width(), this.v,
                    this.centerPatchSize.width(), this.cornerPatchSize.height(), this.textureSize.width(),
                    this.textureSize.height());
            context.blit(this.texture, x + this.cornerPatchSize.width(), y + height - this.cornerPatchSize.height(),
                    width - doubleCornerWidth, this.cornerPatchSize.height(), this.u + this.cornerPatchSize.width(),
                    this.v + bottomEdge, this.centerPatchSize.width(), this.cornerPatchSize.height(),
                    this.textureSize.width(), this.textureSize.height());
        }

        if (height > doubleCornerHeight) {
            context.blit(this.texture, x, y + this.cornerPatchSize.height(), this.cornerPatchSize.width(),
                    height - doubleCornerHeight, this.u, this.v + this.cornerPatchSize.height(),
                    this.cornerPatchSize.width(), this.centerPatchSize.height(), this.textureSize.width(),
                    this.textureSize.height());
            context.blit(this.texture, x + width - this.cornerPatchSize.width(), y + this.cornerPatchSize.height(),
                    this.cornerPatchSize.width(), height - doubleCornerHeight, this.u + rightEdge,
                    this.v + this.cornerPatchSize.height(), this.cornerPatchSize.width(), this.centerPatchSize.height(),
                    this.textureSize.width(), this.textureSize.height());
        }
    }

    protected void drawRepeated(UIGuiGraphics context, int x, int y, int width, int height) {
        int doubleCornerHeight = this.cornerPatchSize.height() * 2;
        int doubleCornerWidth = this.cornerPatchSize.width() * 2;

        int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
        int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();

        if (width > doubleCornerWidth && height > doubleCornerHeight) {
            int leftoverHeight = height - doubleCornerHeight;
            while (leftoverHeight > 0) {
                int drawHeight = Math.min(this.centerPatchSize.height(), leftoverHeight);

                int leftoverWidth = width - doubleCornerWidth;
                while (leftoverWidth > 0) {
                    int drawWidth = Math.min(this.centerPatchSize.width(), leftoverWidth);
                    context.blit(this.texture, x + this.cornerPatchSize.width() + leftoverWidth - drawWidth,
                            y + this.cornerPatchSize.height() + leftoverHeight - drawHeight, drawWidth, drawHeight,
                            this.u + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth,
                            this.v + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
                            drawWidth, drawHeight, this.textureSize.width(), this.textureSize.height());

                    leftoverWidth -= this.centerPatchSize.width();
                }
                leftoverHeight -= this.centerPatchSize.height();
            }
        }

        if (width > doubleCornerWidth) {
            int leftoverWidth = width - doubleCornerWidth;
            while (leftoverWidth > 0) {
                int drawWidth = Math.min(this.centerPatchSize.width(), leftoverWidth);

                context.blit(this.texture, x + this.cornerPatchSize.width() + leftoverWidth - drawWidth, y, drawWidth,
                        this.cornerPatchSize.height(),
                        this.u + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth, this.v,
                        drawWidth, this.cornerPatchSize.height(), this.textureSize.width(), this.textureSize.height());
                context.blit(this.texture, x + this.cornerPatchSize.width() + leftoverWidth - drawWidth,
                        y + height - this.cornerPatchSize.height(), drawWidth, this.cornerPatchSize.height(),
                        this.u + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth,
                        this.v + bottomEdge, drawWidth, this.cornerPatchSize.height(), this.textureSize.width(),
                        this.textureSize.height());

                leftoverWidth -= this.centerPatchSize.width();
            }
        }

        if (height > doubleCornerHeight) {
            int leftoverHeight = height - doubleCornerHeight;
            while (leftoverHeight > 0) {
                int drawHeight = Math.min(this.centerPatchSize.height(), leftoverHeight);
                context.blit(this.texture, x, y + this.cornerPatchSize.height() + leftoverHeight - drawHeight,
                        this.cornerPatchSize.width(), drawHeight, this.u,
                        this.v + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
                        this.cornerPatchSize.width(), drawHeight, this.textureSize.width(), this.textureSize.height());
                context.blit(this.texture, x + width - this.cornerPatchSize.width(),
                        y + this.cornerPatchSize.height() + leftoverHeight - drawHeight, this.cornerPatchSize.width(),
                        drawHeight, this.u + rightEdge,
                        this.v + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
                        this.cornerPatchSize.width(), drawHeight, this.textureSize.width(), this.textureSize.height());

                leftoverHeight -= this.centerPatchSize.height();
            }
        }
    }

    public static void draw(ResourceLocation texture, UIGuiGraphics context, int x, int y, int width, int height) {
        ifPresent(texture, ninePatchTexture -> ninePatchTexture.draw(context, x, y, width, height));
    }

    public static void draw(ResourceLocation texture, UIGuiGraphics context, PositionedRectangle rectangle) {
        ifPresent(texture, ninePatchTexture -> ninePatchTexture.draw(context, rectangle));
    }

    private static void ifPresent(ResourceLocation texture, Consumer<NinePatchTexture> action) {
        if (!MetadataLoader.LOADED_TEXTURES.containsKey(texture)) return;
        action.accept(MetadataLoader.LOADED_TEXTURES.get(texture));
    }

    public static class MetadataLoader extends SimpleJsonResourceReloadListener {

        private static final Map<ResourceLocation, NinePatchTexture> LOADED_TEXTURES = new HashMap<>();

        private static final Gson GSON = new GsonBuilder()
                .setLenient()
                .setPrettyPrinting()
                .create();
        private static final String LOCATION = "gtceu/nine_patch_textures";

        public MetadataLoader() {
            super(GSON, LOCATION);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager,
                             ProfilerFiller profiler) {
            LOADED_TEXTURES.clear();
            prepared.forEach((resourceId, jsonElement) -> {
                if (!(jsonElement instanceof JsonObject object)) return;

                var texture = new ResourceLocation(GsonHelper.getAsString(object, "texture"));
                var textureSize = Size.of(GsonHelper.getAsInt(object, "texture_width"),
                        GsonHelper.getAsInt(object, "texture_height"));

                int u = GsonHelper.getAsInt(object, "u", 0), v = GsonHelper.getAsInt(object, "v", 0);
                boolean repeat = GsonHelper.getAsBoolean(object, "repeat");

                if (object.has("corner_patch_size")) {
                    var cornerPatchObject = GsonHelper.getAsJsonObject(object, "corner_patch_size");
                    var centerPatchObject = GsonHelper.getAsJsonObject(object, "center_patch_size");

                    var cornerPatchSize = Size.of(GsonHelper.getAsInt(cornerPatchObject, "width"),
                            GsonHelper.getAsInt(cornerPatchObject, "height"));
                    var centerPatchSize = Size.of(GsonHelper.getAsInt(centerPatchObject, "width"),
                            GsonHelper.getAsInt(centerPatchObject, "height"));

                    LOADED_TEXTURES.put(resourceId,
                            new NinePatchTexture(texture, u, v, cornerPatchSize, centerPatchSize, textureSize, repeat));
                } else {
                    var patchSizeObject = GsonHelper.getAsJsonObject(object, "patch_size");
                    var patchSize = Size.of(GsonHelper.getAsInt(patchSizeObject, "width"),
                            GsonHelper.getAsInt(patchSizeObject, "height"));

                    LOADED_TEXTURES.put(resourceId,
                            new NinePatchTexture(texture, u, v, patchSize, textureSize, repeat));
                }
            });
        }
    }
}
