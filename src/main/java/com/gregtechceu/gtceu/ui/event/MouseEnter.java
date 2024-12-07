package com.gregtechceu.gtceu.ui.event;

import com.gregtechceu.gtceu.ui.util.EventStream;

public interface MouseEnter {
    void onMouseEnter();

    static EventStream<MouseEnter> newStream() {
        return new EventStream<>(subscribers -> () -> {
            for (var subscriber : subscribers) {
                subscriber.onMouseEnter();
            }
        });
    }
}
