package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.PositionedRectangle;
import com.gregtechceu.gtceu.api.ui.core.Size;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;

import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import com.gregtechceu.gtceu.utils.SupplierMemoizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NinePatchTexture extends ResourceTexture {

    private int offsetX, offsetY;
    private Size cornerPatchSize;
    private Size centerPatchSize;
    private Size textureSize;
    private boolean repeat;

    public NinePatchTexture(ResourceLocation imageLocation, int u, int v, Size cornerPatchSize, Size centerPatchSize,
                            Size textureSize, boolean repeat) {
        super(imageLocation, u, v, textureSize.width(), textureSize.height());
        this.offsetX = u;
        this.offsetY = v;
        this.textureSize = textureSize;
        this.cornerPatchSize = cornerPatchSize;
        this.centerPatchSize = centerPatchSize;
        this.repeat = repeat;
    }

    public NinePatchTexture(ResourceLocation imageLocation, int u, int v, Size patchSize, Size textureSize, boolean repeat) {
        this(imageLocation, u, v, patchSize, patchSize, textureSize, repeat);
    }

    public NinePatchTexture(ResourceLocation imageLocation, Size patchSize, Size textureSize, boolean repeat) {
        this(imageLocation, 0, 0, patchSize, textureSize, repeat);
    }

    public void draw(UIGuiGraphics context, PositionedRectangle rectangle) {
        this.draw(context, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
    }

    public void draw(UIGuiGraphics context, float x, float y, float width, float height) {
        int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
        int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();

        context.blit(this.imageLocation, x, y, this.offsetX, this.offsetY, this.cornerPatchSize.width(), this.cornerPatchSize.height(),
                this.textureSize.width(), this.textureSize.height());
        context.blit(this.imageLocation, x + width - this.cornerPatchSize.width(), y, this.offsetX + rightEdge, this.offsetY,
                this.cornerPatchSize.width(), this.cornerPatchSize.height(), this.textureSize.width(),
                this.textureSize.height());
        context.blit(this.imageLocation, x, y + height - this.cornerPatchSize.height(), this.offsetX, this.offsetY + bottomEdge,
                this.cornerPatchSize.width(), this.cornerPatchSize.height(), this.textureSize.width(),
                this.textureSize.height());
        context.blit(this.imageLocation, x + width - this.cornerPatchSize.width(), y + height - this.cornerPatchSize.height(),
                this.offsetX + rightEdge, this.offsetY + bottomEdge, this.cornerPatchSize.width(), this.cornerPatchSize.height(),
                this.textureSize.width(), this.textureSize.height());

        if (this.repeat) {
            this.drawRepeated(context, x, y, width, height);
        } else {
            this.drawStretched(context, x, y, width, height);
        }
    }

    protected void drawStretched(UIGuiGraphics context, float x, float y, float width, float height) {
        int doubleCornerHeight = this.cornerPatchSize.height() * 2;
        int doubleCornerWidth = this.cornerPatchSize.width() * 2;

        int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
        int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();

        if (width > doubleCornerWidth && height > doubleCornerHeight) {
            context.blit(this.imageLocation, x + this.cornerPatchSize.width(), y + this.cornerPatchSize.height(),
                    width - doubleCornerWidth, height - doubleCornerHeight, this.offsetX + this.cornerPatchSize.width(),
                    this.offsetY + this.cornerPatchSize.height(), this.centerPatchSize.width(), this.centerPatchSize.height(),
                    this.textureSize.width(), this.textureSize.height());
        }

        if (width > doubleCornerWidth) {
            context.blit(this.imageLocation, x + this.cornerPatchSize.width(), y, width - doubleCornerWidth,
                    this.cornerPatchSize.height(), this.offsetX + this.cornerPatchSize.width(), this.offsetY,
                    this.centerPatchSize.width(), this.cornerPatchSize.height(), this.textureSize.width(),
                    this.textureSize.height());
            context.blit(this.imageLocation, x + this.cornerPatchSize.width(), y + height - this.cornerPatchSize.height(),
                    width - doubleCornerWidth, this.cornerPatchSize.height(), this.offsetX + this.cornerPatchSize.width(),
                    this.offsetY + bottomEdge, this.centerPatchSize.width(), this.cornerPatchSize.height(),
                    this.textureSize.width(), this.textureSize.height());
        }

        if (height > doubleCornerHeight) {
            context.blit(this.imageLocation, x, y + this.cornerPatchSize.height(), this.cornerPatchSize.width(),
                    height - doubleCornerHeight, this.offsetX, this.offsetY + this.cornerPatchSize.height(),
                    this.cornerPatchSize.width(), this.centerPatchSize.height(), this.textureSize.width(),
                    this.textureSize.height());
            context.blit(this.imageLocation, x + width - this.cornerPatchSize.width(), y + this.cornerPatchSize.height(),
                    this.cornerPatchSize.width(), height - doubleCornerHeight, this.offsetX + rightEdge,
                    this.offsetY + this.cornerPatchSize.height(), this.cornerPatchSize.width(), this.centerPatchSize.height(),
                    this.textureSize.width(), this.textureSize.height());
        }
    }

    protected void drawRepeated(UIGuiGraphics context, float x, float y, float width, float height) {
        int doubleCornerHeight = this.cornerPatchSize.height() * 2;
        int doubleCornerWidth = this.cornerPatchSize.width() * 2;

        int rightEdge = this.cornerPatchSize.width() + this.centerPatchSize.width();
        int bottomEdge = this.cornerPatchSize.height() + this.centerPatchSize.height();

        if (width > doubleCornerWidth && height > doubleCornerHeight) {
            float leftoverHeight = height - doubleCornerHeight;
            while (leftoverHeight > 0) {
                float drawHeight = Math.min(this.centerPatchSize.height(), leftoverHeight);

                float leftoverWidth = width - doubleCornerWidth;
                while (leftoverWidth > 0) {
                    float drawWidth = Math.min(this.centerPatchSize.width(), leftoverWidth);
                    context.blit(this.imageLocation, x + this.cornerPatchSize.width() + leftoverWidth - drawWidth,
                            y + this.cornerPatchSize.height() + leftoverHeight - drawHeight, drawWidth, drawHeight,
                            this.offsetX + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth,
                            this.offsetY + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
                            drawWidth, drawHeight, this.textureSize.width(), this.textureSize.height());

                    leftoverWidth -= this.centerPatchSize.width();
                }
                leftoverHeight -= this.centerPatchSize.height();
            }
        }

        if (width > doubleCornerWidth) {
            float leftoverWidth = width - doubleCornerWidth;
            while (leftoverWidth > 0) {
                float drawWidth = Math.min(this.centerPatchSize.width(), leftoverWidth);

                context.blit(this.imageLocation, x + this.cornerPatchSize.width() + leftoverWidth - drawWidth, y, drawWidth,
                        this.cornerPatchSize.height(),
                        this.offsetX + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth, this.offsetY,
                        drawWidth, this.cornerPatchSize.height(), this.textureSize.width(), this.textureSize.height());
                context.blit(this.imageLocation, x + this.cornerPatchSize.width() + leftoverWidth - drawWidth,
                        y + height - this.cornerPatchSize.height(), drawWidth, this.cornerPatchSize.height(),
                        this.offsetX + this.cornerPatchSize.width() + this.centerPatchSize.width() - drawWidth,
                        this.offsetY + bottomEdge, drawWidth, this.cornerPatchSize.height(), this.textureSize.width(),
                        this.textureSize.height());

                leftoverWidth -= this.centerPatchSize.width();
            }
        }

        if (height > doubleCornerHeight) {
            float leftoverHeight = height - doubleCornerHeight;
            while (leftoverHeight > 0) {
                float drawHeight = Math.min(this.centerPatchSize.height(), leftoverHeight);
                context.blit(this.imageLocation, x, y + this.cornerPatchSize.height() + leftoverHeight - drawHeight,
                        this.cornerPatchSize.width(), drawHeight, this.offsetX,
                        this.offsetY + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
                        this.cornerPatchSize.width(), drawHeight, this.textureSize.width(), this.textureSize.height());
                context.blit(this.imageLocation, x + width - this.cornerPatchSize.width(),
                        y + this.cornerPatchSize.height() + leftoverHeight - drawHeight, this.cornerPatchSize.width(),
                        drawHeight, this.offsetX + rightEdge,
                        this.offsetY + this.cornerPatchSize.height() + this.centerPatchSize.height() - drawHeight,
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

    @Override
    public void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height) {
        this.draw(graphics, x, y, width, height);
    }

    @ApiStatus.Internal
    public static NinePatchTexture get(ResourceLocation id) {
        return MetadataLoader.LOADED_TEXTURES.computeIfAbsent(id,
                invalidId -> new NinePatchTexture(invalidId, 0, 0, Size.zero(), Size.zero(), false));
    }

    public static NinePatchTexture parse(Element element) {
        UIParsing.expectAttributes(element, "texture");
        ResourceLocation id = UIParsing.parseResourceLocation(element.getAttributeNode("texture"));
        return NinePatchTexture.get(id);
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

                    if (LOADED_TEXTURES.containsKey(resourceId)) {
                        update(LOADED_TEXTURES.get(resourceId), texture, textureSize, u, v, repeat,
                                cornerPatchSize, centerPatchSize);
                    } else {
                        LOADED_TEXTURES.put(resourceId,
                                new NinePatchTexture(texture, u, v, cornerPatchSize, centerPatchSize, textureSize, repeat));
                    }
                } else {
                    var patchSizeObject = GsonHelper.getAsJsonObject(object, "patch_size");
                    var patchSize = Size.of(GsonHelper.getAsInt(patchSizeObject, "width"),
                            GsonHelper.getAsInt(patchSizeObject, "height"));

                    if (LOADED_TEXTURES.containsKey(resourceId)) {
                        update(LOADED_TEXTURES.get(resourceId), texture, textureSize, u, v, repeat, patchSize, patchSize);
                    } else {
                        LOADED_TEXTURES.put(resourceId,
                                new NinePatchTexture(texture, u, v, patchSize, textureSize, repeat));
                    }
                }
            });
        }

        public void update(NinePatchTexture tex, ResourceLocation texture,
                           Size textureSize, int u, int v, boolean repeat,
                           Size cornerPatchSize, Size centerPatchSize) {
            tex.imageLocation = texture;
            tex.offsetX = u;
            tex.offsetY = v;
            tex.cornerPatchSize = cornerPatchSize;
            tex.centerPatchSize = centerPatchSize;
            tex.textureSize = textureSize;
            tex.repeat = repeat;
        }

    }
}
