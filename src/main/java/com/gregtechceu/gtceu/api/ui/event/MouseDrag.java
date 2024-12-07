package com.gregtechceu.gtceu.api.ui.event;

import com.gregtechceu.gtceu.api.ui.util.EventStream;

public interface MouseDrag {
    boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button);

    static EventStream<MouseDrag> newStream() {
        return new EventStream<>(subscribers -> (mouseX, mouseY, deltaX, deltaY, button) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
            }
            return anyTriggered;
        });
    }
}
