package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Supplier;

public class UITextures {

    private UITextures() {}

    public static ResourceTexture resource(ResourceLocation texture, int u, int v, int width, int height) {
        return new ResourceTexture(texture, u, v, width, height);
    }

    public static ResourceTexture resource(ResourceLocation texture) {
        return new ResourceTexture(texture);
    }

    public static ProgressTexture progress(UITexture empty, UITexture full) {
        return new ProgressTexture(empty, full);
    }

    public static UITextureGroup group() {
        return new UITextureGroup();
    }

    public static UITextureGroup group(UITexture... textures) {
        return new UITextureGroup(textures);
    }

    public static UITextureGroup group(Collection<UITexture> textures) {
        return new UITextureGroup(textures);
    }

    public static ItemStackTexture item(ItemStack stack) {
        return new ItemStackTexture(stack);
    }

    public static DynamicTexture dynamic(Supplier<UITexture> texture) {
        return new DynamicTexture(texture);
    }

    public static NinePatchTexture ninePatch(ResourceLocation id) {
        return NinePatchTexture.get(id);
    }

    public static TextTexture text(Component text) {
        return new TextTexture(text);
    }

    public static ColorBorderTexture colorBorder(Color color, int border) {
        return new ColorBorderTexture(color, border);
    }
}
