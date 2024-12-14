package com.gregtechceu.gtceu.api.ui.core;

import com.gregtechceu.gtceu.api.ui.parsing.UIModelParsingException;

import com.gregtechceu.gtceu.api.ui.texture.ColorBorderTexture;
import com.gregtechceu.gtceu.api.ui.texture.ColorRectTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public record Color(float red, float green, float blue, float alpha) implements Animatable<Color> {

    public static final Color BLACK = Color.ofRgb(0);
    public static final Color WHITE = Color.ofRgb(0xFFFFFF);
    public static final Color RED = Color.ofRgb(0xFF0000);
    public static final Color YELLOW = Color.ofRgb(0xFFFF00);
    public static final Color GREEN = Color.ofRgb(0x00FF00);
    public static final Color BLUE = Color.ofRgb(0x0000FF);
    public static final Color HOVER_GRAY = Color.ofArgb(0x80FFFFFF);


    public static final Color T_WHITE = Color.ofArgb(0x88ffffff);
    public static final Color T_BLACK = Color.ofArgb(0x44222222);
    public static final Color SEAL_BLACK = Color.ofArgb(0xFF313638);
    public static final Color T_SEAL_BLACK = Color.ofArgb(0x88313638);
    public static final Color GRAY = Color.ofArgb(0xff666666);
    public static final Color T_GRAY = Color.ofArgb(0x66666666);
    public static final Color DARK_GRAY = Color.ofArgb(0xff444444);
    public static final Color T_DARK_GRAY = Color.ofArgb(0x44444444);
    public static final Color LIGHT_GRAY = Color.ofArgb(0xffaaaaaa);
    public static final Color T_LIGHT_GRAY = Color.ofArgb(0x88aaaaaa);

    public static final Color T_GREEN = Color.ofArgb(0x8833ff00);
    public static final Color T_RED = Color.ofArgb(0x889d0122);
    public static final Color BRIGHT_RED = Color.ofArgb(0xffFF0000);
    public static final Color T_BRIGHT_RED = Color.ofArgb(0x88FF0000);
    public static final Color T_YELLOW = Color.ofArgb(0x88ffff33);
    public static final Color CYAN = Color.ofArgb(0xff337777);
    public static final Color T_CYAN = Color.ofArgb(0x88337777);
    public static final Color PURPLE = Color.ofArgb(0xff9933ff);
    public static final Color T_PURPLE = Color.ofArgb(0x889933ff);
    public static final Color PINK = Color.ofArgb(0xffff33ff);
    public static final Color T_PINK = Color.ofArgb(0x88ff33ff);
    public static final Color T_BLUE = Color.ofArgb(0x884852ff);
    public static final Color ORANGE = Color.ofArgb(0xffff8800);
    public static final Color T_ORANGE = Color.ofArgb(0x88ff8800);
    public static final Color BROWN = Color.ofArgb(0xffaa7744);
    public static final Color T_BROWN = Color.ofArgb(0x88aa7744);
    public static final Color LIME = Color.ofArgb(0xff77aa44);
    public static final Color T_LIME = Color.ofArgb(0x8877aa44);
    public static final Color MAGENTA = Color.ofArgb(0xffaa44aa);
    public static final Color T_MAGENTA = Color.ofArgb(0x88aa44aa);
    public static final Color LIGHT_BLUE = Color.ofArgb(0xff44aaff);
    public static final Color T_LIGHT_BLUE = Color.ofArgb(0x8844aaff);

    private static final Map<String, Color> NAMED_TEXT_COLORS = Stream.of(ChatFormatting.values())
            .filter(ChatFormatting::isColor)
            .collect(ImmutableMap.toImmutableMap(formatting -> {
                return formatting.getName().replace("_", "-");
            }, Color::ofFormatting));

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    public static Color ofArgb(int argb) {
        return new Color(
                ((argb >> 16) & 0xFF) / 255f,
                ((argb >> 8) & 0xFF) / 255f,
                (argb & 0xFF) / 255f,
                (argb >>> 24) / 255f);
    }

    public static Color ofRgb(int rgb) {
        return new Color(
                ((rgb >> 16) & 0xFF) / 255f,
                ((rgb >> 8) & 0xFF) / 255f,
                (rgb & 0xFF) / 255f,
                1f);
    }

    public static Color ofHsv(float hue, float saturation, float value) {
        // we call .5e-7f the magic "do not turn a hue value of 1f into yellow" constant

        return ofRgb(Mth.hsvToRgb(hue - .5e-7f, saturation, value));
    }

    public static Color ofHsv(float hue, float saturation, float value, float alpha) {
        // we call .5e-7f the magic "do not turn a hue value of 1f into yellow" constant
        return ofArgb((int) (alpha * 255) << 24 | Mth.hsvToRgb(hue - .5e-7f, saturation, value));
    }

    public static Color ofFormatting(@NotNull ChatFormatting formatting) {
        var colorValue = formatting.getColor();
        return ofRgb(colorValue == null ? 0 : colorValue);
    }

    public static Color ofDye(@NotNull DyeColor dyeColor) {
        var components = dyeColor.getTextureDiffuseColors();
        return new Color(components[0], components[1], components[2]);
    }

    public int rgb() {
        return (int) (this.red * 255) << 16 | (int) (this.green * 255) << 8 | (int) (this.blue * 255);
    }

    public int argb() {
        return (int) (this.alpha * 255) << 24 | (int) (this.red * 255) << 16 | (int) (this.green * 255) << 8 |
                (int) (this.blue * 255);
    }

    public ColorRectTexture rectTexture() {
        return UITextures.colorRect(this);
    }

    public ColorBorderTexture borderTexture(int border) {
        return UITextures.colorBorder(this, border);
    }

    public float[] hsv() {
        float hue, saturation, value;

        float cmax = Math.max(Math.max(this.red, this.green), this.blue);
        float cmin = Math.min(Math.min(this.red, this.green), this.blue);

        value = cmax;
        if (cmax != 0) {
            saturation = (cmax - cmin) / cmax;
        } else {
            saturation = 0;
        }

        if (saturation == 0) {
            hue = 0;
        } else {
            float redc = (cmax - this.red) / (cmax - cmin);
            float greenc = (cmax - this.green) / (cmax - cmin);
            float bluec = (cmax - this.blue) / (cmax - cmin);

            if (this.red == cmax) {
                hue = bluec - greenc;
            } else if (this.green == cmax)
                hue = 2.0f + redc - bluec;
            else {
                hue = 4.0f + greenc - redc;
            }

            hue = hue / 6.0f;
            if (hue < 0) hue = hue + 1.0f;
        }

        return new float[] { hue, saturation, value, this.alpha };
    }

    public String asHexString(boolean includeAlpha) {
        return includeAlpha ? String.format("#%08X", this.argb()) : String.format("#%06X", this.rgb());
    }

    @Override
    public Color interpolate(Color next, float delta) {
        return new Color(
                Mth.lerp(delta, this.red, next.red),
                Mth.lerp(delta, this.green, next.green),
                Mth.lerp(delta, this.blue, next.blue),
                Mth.lerp(delta, this.alpha, next.alpha));
    }

    /**
     * Tries to interpret the given node's text content as a color
     * in {@code #RRGGBB} or {@code #AARRGGBB} format, or as
     * the name of a text color
     *
     * @return The parsed color as an unsigned integer
     * @throws UIModelParsingException If the text content does not match
     *                                 the expected color format
     */
    public static Color parse(Node node) {
        var text = node.getTextContent().strip();

        if (!text.startsWith("#")) {
            var color = NAMED_TEXT_COLORS.get(text);
            if (color != null) {
                return color;
            } else {
                throw new UIModelParsingException("Invalid color value '" + text +
                        "', expected hex color of format #RRGGBB or #AARRGGBB or named text color");
            }
        } else {
            if (text.matches("#([A-Fa-f\\d]{2}){3,4}")) {
                return text.length() == 7 ? Color.ofRgb(Integer.parseUnsignedInt(text.substring(1), 16)) :
                        Color.ofArgb(Integer.parseUnsignedInt(text.substring(1), 16));
            } else {
                throw new UIModelParsingException("Invalid color value '" + text +
                        "', expected hex color of format #RRGGBB or #AARRGGBB or named text color");
            }
        }
    }

    public static int parseAndPack(Node node) {
        return parse(node).argb();
    }
}
