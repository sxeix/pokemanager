package com.sxeix.pokemanager.domain.messaging;

public interface MessageBrokerPublisher {

    void send(String eventType, String payload);

}
