package com.gregtechceu.gtceu.api.ui.util;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MountingHelper {
    protected final ComponentSink sink;
    protected final List<UIComponent> lateChildren;

    protected MountingHelper(ComponentSink sink, List<UIComponent> children) {
        this.sink = sink;
        this.lateChildren = children;
    }

    public static MountingHelper mountEarly(ComponentSink sink, List<UIComponent> children, Consumer<UIComponent> layoutFunc) {
        var lateChildren = new ArrayList<UIComponent>();

        for (var child : children) {
            if (!child.positioning().get().isRelative()) {
                sink.accept(child, layoutFunc);
            } else {
                lateChildren.add(child);
            }
        }

        return new MountingHelper(sink, lateChildren);
    }

    public void mountLate() {
        for (var child : this.lateChildren) {
            this.sink.accept(child, component -> {throw new IllegalStateException("A layout-positioned child was mounted late");});
        }
        this.lateChildren.clear();
    }

    public interface ComponentSink {
        void accept(@Nullable UIComponent child, Consumer<UIComponent> layoutFunc);
    }
}
