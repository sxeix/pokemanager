package com.sxeix.pokemanager.infrastructure.persistence;

import com.sxeix.pokemanager.domain.repository.IdempotencyRecordRepository;
import com.sxeix.pokemanager.domain.model.IdempotencyRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdempotencyRecordPersistenceAdapter implements IdempotencyRecordRepository {

    // TODO: should switch this out for redis
    private final JpaIdempotencyRecordRepository jpaIdempotencyRecordRepository;

    @Override
    public IdempotencyRecord saveAndFlush(final IdempotencyRecord idempotencyRecord) {
        return jpaIdempotencyRecordRepository.saveAndFlush(idempotencyRecord);
    }

    @Override
    public Optional<IdempotencyRecord> findById(final String id) {
        return jpaIdempotencyRecordRepository.findById(id);
    }

}
