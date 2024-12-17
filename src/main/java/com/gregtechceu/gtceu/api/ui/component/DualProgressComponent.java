package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.parsing.UIModel;
import com.gregtechceu.gtceu.api.ui.parsing.UIParsing;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.DoubleSupplier;

public class DualProgressComponent extends StackLayout {

    private DoubleSupplier progressSupplier;
    @Setter
    private double splitPoint;
    @Getter
    @Setter
    private ProgressComponent texture1;
    @Getter
    @Setter
    private ProgressComponent texture2;

    protected DualProgressComponent() {
        this(ProgressComponent.JEIProgress, 0.5);
    }

    protected DualProgressComponent(DoubleSupplier progress, double splitPoint) {
        this(new ProgressComponent(ProgressComponent.JEIProgress),
                new ProgressComponent(ProgressComponent.JEIProgress), progress, splitPoint);
    }

    public DualProgressComponent(ProgressComponent texture1, ProgressComponent texture2, DoubleSupplier progress,
                                 double splitPoint) {
        super(Sizing.content(), Sizing.content());
        this.progressSupplier = progress;
        this.splitPoint = splitPoint;
        this.texture1 = texture1.progressSupplier(
                () -> progress.getAsDouble() >= splitPoint ? 1.0 : (1.0 / splitPoint) * progress.getAsDouble());
        this.texture2 = texture2.progressSupplier(() -> progress.getAsDouble() >= splitPoint ?
                (1.0 / (1 - splitPoint)) * (progress.getAsDouble() - splitPoint) : 0);
        this.child(this.texture1).child(this.texture2);
    }

    public DualProgressComponent texture1(ProgressComponent widget) {
        this.removeChild(texture1);
        this.texture1 = widget.progressSupplier(() -> progressSupplier.getAsDouble() >= splitPoint ? 1.0 :
                (1.0 / splitPoint) * progressSupplier.getAsDouble());
        this.child(texture1);
        return this;
    }

    public DualProgressComponent texture2(ProgressComponent widget) {
        this.removeChild(texture2);
        this.texture2 = widget.progressSupplier(() -> progressSupplier.getAsDouble() >= splitPoint ?
                (1.0 / (1 - splitPoint)) * (progressSupplier.getAsDouble() - splitPoint) : 0);
        this.child(texture2);
        return this;
    }

    public DualProgressComponent progressSupplier(DoubleSupplier progressSupplier) {
        this.progressSupplier = progressSupplier;

        this.clearChildren();
        this.texture1.progressSupplier(() -> progressSupplier.getAsDouble() >= splitPoint ? 1.0 :
                (1.0 / splitPoint) * progressSupplier.getAsDouble());
        this.child(texture1);
        this.texture2.progressSupplier(() -> progressSupplier.getAsDouble() >= splitPoint ?
                (1.0 / (1 - splitPoint)) * (progressSupplier.getAsDouble() - splitPoint) : 0);
        this.child(texture2);

        return this;
    }

    public DualProgressComponent splitPoint(double splitPoint) {
        this.splitPoint = splitPoint;

        this.clearChildren();
        this.texture1.progressSupplier(() -> progressSupplier.getAsDouble() >= splitPoint ? 1.0 :
                (1.0 / splitPoint) * progressSupplier.getAsDouble());
        this.child(texture1);
        this.texture2.progressSupplier(() -> progressSupplier.getAsDouble() >= splitPoint ?
                (1.0 / (1 - splitPoint)) * (progressSupplier.getAsDouble() - splitPoint) : 0);
        this.child(texture2);

        return this;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.expectChildren(element, children, "texture1", "texture2");
        texture1(model.parseComponent(ProgressComponent.class, children.get("texture1")));
        texture2(model.parseComponent(ProgressComponent.class, children.get("texture2")));

        UIParsing.apply(children, "split-point", UIParsing::parseDouble, this::splitPoint);
    }
}
