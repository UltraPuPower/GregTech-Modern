package com.gregtechceu.gtceu.api.ui.texture;

import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.DoubleSupplier;

@Accessors(fluent = true, chain = true)
public class ProgressTexture extends TransformTexture {

    @Nullable
    @Setter
    protected DoubleSupplier progressSupplier;
    @Setter
    protected FillDirection fillDirection = FillDirection.LEFT_TO_RIGHT;
    @Getter
    @Setter
    protected UITexture emptyBarArea;
    @Getter
    @Setter
    protected UITexture filledBarArea;

    protected double progress;

    @Setter
    private boolean demo;

    public ProgressTexture(UITexture emptyBarArea, UITexture filledBarArea) {
        this.emptyBarArea = emptyBarArea;
        this.filledBarArea = filledBarArea;
    }

    public void setProgress(double progress) {
        this.progress = Mth.clamp(0.0, progress, 1.0);
    }

    @Override
    public void updateTick() {
        if (emptyBarArea != null) {
            emptyBarArea.updateTick();
        }
        if (filledBarArea != null) {
            filledBarArea.updateTick();
        }
        if (demo) {
            progress = Math.abs(System.currentTimeMillis() % 2000) / 2000.0;
        } else if (progressSupplier != null) {
            progress = progressSupplier.getAsDouble();
        }
    }

    @Override
    protected void drawInternal(UIGuiGraphics graphics, int mouseX, int mouseY, float x, float y, float width, float height) {
        if (emptyBarArea != null) {
            emptyBarArea.draw(graphics, mouseX, mouseY, x, y, width, height);
        }
        if (filledBarArea != null) {
            float drawnU = (float) fillDirection.getDrawnU(progress);
            float drawnV = (float) fillDirection.getDrawnV(progress);
            float drawnWidth = (float) fillDirection.getDrawnWidth(progress);
            float drawnHeight = (float) fillDirection.getDrawnHeight(progress);
            int x1 = (int) (x + drawnU * width);
            int y1 = (int) (y + drawnV * height);
            int w1 = (int) (width * drawnWidth);
            int h1 = (int) (height * drawnHeight);

            filledBarArea.drawSubArea(graphics, x1, y1, w1, h1, drawnU, drawnV,
                    ((drawnWidth * width)) / (width),
                    ((drawnHeight * height)) / (height));
        }
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.expectChildren(element, children, "empty", "filled");
        UIParsing.apply(children, "fill-direction", UIParsing.parseEnum(FillDirection.class), this::fillDirection);

        var emptyElement = children.get("empty");
        this.emptyBarArea(model.parseTexture(UITexture.class, emptyElement));

        var filledElement = children.get("empty");
        this.filledBarArea(model.parseTexture(UITexture.class, filledElement));

        UIParsing.apply(children, "progress", UIParsing::parseDouble, this::setProgress);
    }

    public static class Auto extends ProgressTexture {

        public Auto(UITexture emptyBarArea, UITexture filledBarArea) {
            super(emptyBarArea, filledBarArea);
        }

        @Override
        public void updateTick() {
            progress = Math.abs(System.currentTimeMillis() % 2000) / 2000.0;
        }

    }

    public enum FillDirection {
        LEFT_TO_RIGHT {
            @Override
            public double getDrawnHeight(double progress) {
                return 1.0;
            }
        },
        RIGHT_TO_LEFT {
            @Override
            public double getDrawnU(double progress) {
                return 1.0 - progress;
            }

            @Override
            public double getDrawnHeight(double progress) {
                return 1.0;
            }
        },
        UP_TO_DOWN {
            @Override
            public double getDrawnWidth(double progress) {
                return 1.0;
            }
        },
        DOWN_TO_UP {
            @Override
            public double getDrawnV(double progress) {
                return 1.0 - progress;
            }

            @Override
            public double getDrawnWidth(double progress) {
                return 1.0;
            }
        },

        ALWAYS_FULL {
            @Override
            public double getDrawnHeight(double progress) {
                return 1.0;
            }

            @Override
            public double getDrawnWidth(double progress) {
                return 1.0;
            }
        };

        public double getDrawnU(double progress) {
            return 0.0;
        }

        public double getDrawnV(double progress) {
            return 0.0;
        }

        public double getDrawnWidth(double progress) {
            return progress;
        }

        public double getDrawnHeight(double progress) {
            return progress;
        }
    }

}
