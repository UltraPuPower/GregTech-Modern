package com.gregtechceu.gtceu.ui.event;

import com.gregtechceu.gtceu.ui.util.EventStream;

public interface FocusLost {
    void onFocusLost();

    static EventStream<FocusLost> newStream() {
        return new EventStream<>(subscribers -> () -> {
            for (var subscriber : subscribers) {
                subscriber.onFocusLost();
            }
        });
    }
}
