package com.gregtechceu.gtceu.ui.event;

import com.gregtechceu.gtceu.ui.core.UIComponent;
import com.gregtechceu.gtceu.ui.util.EventStream;

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
