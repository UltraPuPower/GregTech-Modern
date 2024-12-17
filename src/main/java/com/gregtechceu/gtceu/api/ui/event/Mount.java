package com.gregtechceu.gtceu.api.ui.event;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.util.EventStream;

public interface Mount {

    void onMount(UIComponent component, int x, int y);

    static EventStream<Mount> newStream() {
        return new EventStream<>(subscribers -> (component, x, y) -> {
            for (var subscriber : subscribers) {
                subscriber.onMount(component, x, y);
            }
        });
    }
}
