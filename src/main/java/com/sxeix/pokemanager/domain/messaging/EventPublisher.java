package com.sxeix.pokemanager.domain.messaging;

import com.sxeix.pokemanager.domain.events.Event;

public interface EventPublisher {
    void publish(Event event);
}
