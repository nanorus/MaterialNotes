package com.example.nanorus.todo.bus;

import com.squareup.otto.Bus;

public class EventBus {
    private static Bus sBus = null;

    public static Bus getBus() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }
}
