package com.gregtechceu.gtceu.ui.event;

import com.gregtechceu.gtceu.ui.util.EventStream;

public interface CharTyped {
    boolean onCharTyped(char chr, int modifiers);

    static EventStream<CharTyped> newStream() {
        return new EventStream<>(subscribers -> (chr, modifiers) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.onCharTyped(chr, modifiers);
            }
            return anyTriggered;
        });
    }
}
