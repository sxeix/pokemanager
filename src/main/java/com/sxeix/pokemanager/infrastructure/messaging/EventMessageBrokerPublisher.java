package com.sxeix.pokemanager.infrastructure.messaging;

import com.google.gson.Gson;
import com.sxeix.pokemanager.domain.messaging.MessageBrokerPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMessageBrokerPublisher implements MessageBrokerPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void send(String eventType, String payload) {

        try {
            var clazz = Class.forName(eventType);
            var event = new Gson().fromJson(payload, clazz);
            applicationEventPublisher.publishEvent(event);
        } catch (ClassNotFoundException e) {
            // TODO: LOG SOMETHING
        }

    }

}
