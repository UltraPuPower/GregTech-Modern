package com.gregtechceu.gtceu.api.ui.event;

import com.gregtechceu.gtceu.api.ui.util.EventStream;

public interface MouseMoved {

    boolean onMouseMoved(double mouseX, double mouseY);

    static EventStream<MouseMoved> newStream() {
        return new EventStream<>(subscribers -> (mouseX, mouseY) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.onMouseMoved(mouseX, mouseY);
            }
            return anyTriggered;
        });
    }
}
