package com.gregtechceu.gtceu.api.ui.event;

import com.gregtechceu.gtceu.api.ui.util.EventStream;

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
