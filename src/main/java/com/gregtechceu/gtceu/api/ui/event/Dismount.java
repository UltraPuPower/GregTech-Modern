package com.gregtechceu.gtceu.api.ui.event;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.util.EventStream;

public interface Dismount {

    void onDismount(UIComponent component, UIComponent.DismountReason reason);

    static EventStream<Dismount> newStream() {
        return new EventStream<>(subscribers -> (component, reason) -> {
            for (var subscriber : subscribers) {
                subscriber.onDismount(component, reason);
            }
        });
    }
}
