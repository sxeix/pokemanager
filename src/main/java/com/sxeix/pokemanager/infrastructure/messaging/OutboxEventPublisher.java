package com.sxeix.pokemanager.infrastructure.messaging;

import com.google.gson.Gson;
import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.events.Event;
import com.sxeix.pokemanager.domain.messaging.EventPublisher;
import com.sxeix.pokemanager.infrastructure.persistence.JpaOutboxEventRepository;
import com.sxeix.pokemanager.infrastructure.persistence.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher implements EventPublisher {

    private final JpaOutboxEventRepository jpaOutboxEventRepository;

    @Override
    public void publish(Event event) {
        var outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(event.getClass().getName());
        outboxEvent.setPayload(new Gson().toJson(event));
        outboxEvent.setStatus(Status.PENDING);
        jpaOutboxEventRepository.save(outboxEvent);
    }

}
