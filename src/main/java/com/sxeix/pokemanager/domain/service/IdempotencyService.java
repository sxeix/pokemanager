package com.sxeix.pokemanager.domain.service;


import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.model.IdempotencyRecord;
import com.sxeix.pokemanager.infrastructure.persistence.IdempotencyRecordPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordPersistenceAdapter idempotencyRecordPersistenceAdapter;

    @Transactional
    public Optional<IdempotencyRecord> find(final String key) {

        return idempotencyRecordPersistenceAdapter.findById(key);
    }

    @Transactional
    public IdempotencyRecord create(final String key) {
        IdempotencyRecord idempotencyRecord = new IdempotencyRecord();
        idempotencyRecord.setIdempotencyKey(key);
        idempotencyRecord.setStatus(Status.STARTED);
        return idempotencyRecordPersistenceAdapter.saveAndFlush(idempotencyRecord);
    }

    @Transactional
    public void complete(final String key, final byte[] protobufBody) {
        idempotencyRecordPersistenceAdapter.findById(key).ifPresent(idempotencyRecord -> {
            idempotencyRecord.setStatus(Status.COMPLETED);
            idempotencyRecord.setResponseBody(protobufBody);
            idempotencyRecord.setUpdatedAt(LocalDateTime.now());
            idempotencyRecordPersistenceAdapter.saveAndFlush(idempotencyRecord);
        });
    }

    @Transactional
    public void complete(final String key) {
        idempotencyRecordPersistenceAdapter.findById(key).ifPresent(idempotencyRecord -> {
            idempotencyRecord.setStatus(Status.COMPLETED);
            idempotencyRecord.setUpdatedAt(LocalDateTime.now());
            idempotencyRecordPersistenceAdapter.saveAndFlush(idempotencyRecord);
        });
    }

    @Transactional
    public void fail(final String key) {
        idempotencyRecordPersistenceAdapter.findById(key).ifPresent(idempotencyRecord -> {
            idempotencyRecord.setStatus(Status.FAILED);
            idempotencyRecordPersistenceAdapter.saveAndFlush(idempotencyRecord);
        });
    }

}
