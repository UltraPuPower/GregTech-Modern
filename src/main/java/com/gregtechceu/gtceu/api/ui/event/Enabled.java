package com.gregtechceu.gtceu.api.ui.event;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.util.EventStream;

public interface Enabled {

    void onEnabled(UIComponent component, boolean newEnabled);

    static EventStream<Enabled> newStream() {
        return new EventStream<>(subscribers -> (component, newEnabled) -> {
            for (var subscriber : subscribers) {
                subscriber.onEnabled(component, newEnabled);
            }
        });
    }
}
