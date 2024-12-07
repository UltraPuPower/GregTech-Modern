package com.gregtechceu.gtceu.ui.event;

import com.gregtechceu.gtceu.ui.util.EventStream;

public interface KeyPress {
    boolean onKeyPress(int keyCode, int scanCode, int modifiers);

    static EventStream<KeyPress> newStream() {
        return new EventStream<>(subscribers -> (keyCode, scanCode, modifiers) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.onKeyPress(keyCode, scanCode, modifiers);
            }
            return anyTriggered;
        });
    }
}
