package com.gregtechceu.gtceu.api.ui.event;

import com.gregtechceu.gtceu.api.ui.util.EventStream;

public interface MouseScroll {

    boolean onMouseScroll(double mouseX, double mouseY, double amount);

    static EventStream<MouseScroll> newStream() {
        return new EventStream<>(subscribers -> (mouseX, mouseY, amount) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.onMouseScroll(mouseX, mouseY, amount);
            }
            return anyTriggered;
        });
    }
}
