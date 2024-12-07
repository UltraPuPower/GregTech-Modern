package com.gregtechceu.gtceu.ui.event;

import com.gregtechceu.gtceu.ui.util.EventStream;

public interface MouseUp {
    boolean onMouseUp(double mouseX, double mouseY, int button);

    static EventStream<MouseUp> newStream() {
        return new EventStream<>(subscribers -> (mouseX, mouseY, button) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.onMouseUp(mouseX, mouseY, button);
            }
            return anyTriggered;
        });
    }
}
