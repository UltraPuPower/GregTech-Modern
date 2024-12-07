package com.gregtechceu.gtceu.api.ui.event;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.util.EventStream;

public interface FocusGained {

    void onFocusGained(UIComponent.FocusSource source);

    static EventStream<FocusGained> newStream() {
        return new EventStream<>(subscribers -> source -> {
            for (var subscriber : subscribers) {
                subscriber.onFocusGained(source);
            }
        });
    }
}
