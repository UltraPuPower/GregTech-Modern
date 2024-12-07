package com.gregtechceu.gtceu.ui.event;

import com.gregtechceu.gtceu.ui.util.EventStream;

public interface MouseLeave {
    void onMouseLeave();

    static EventStream<MouseLeave> newStream() {
        return new EventStream<>(subscribers -> () -> {
            for (var subscriber : subscribers) {
                subscriber.onMouseLeave();
            }
        });
    }
}
