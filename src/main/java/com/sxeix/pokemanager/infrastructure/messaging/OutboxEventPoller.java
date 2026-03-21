package com.sxeix.pokemanager.infrastructure.messaging;

import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.messaging.MessageBrokerPublisher;
import com.sxeix.pokemanager.infrastructure.persistence.JpaOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxEventPoller {

    private final JpaOutboxEventRepository jpaOutboxEventRepository;
    private final MessageBrokerPublisher messageBrokerPublisher;

    @Scheduled(fixedDelay = 5000L) // 5 seconds
    @Transactional
    public void pollEvents() {

        var events = jpaOutboxEventRepository.findNextPendingEvents(PageRequest.of(0, 5));
        if (events.isEmpty()) {
            return;
        }

        for (var event : events) {
            event.setStatus(Status.PROCESSING);
            event.setLockedUntil(LocalDateTime.now().plusMinutes(3));
            messageBrokerPublisher.send(event.getEventType(), event.getPayload());
        }
    }

    @Scheduled(fixedDelay = 30000L) ///  30 seconds
    @Transactional
    public void redriveStuckEvents() {
        // Find events in PROCESSING where lockedUntil < now
        // Reset them to PENDING and increment retryCount
        // jpaOutboxEventRepository.resetTimedOutEvents();
    }


}
