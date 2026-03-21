package com.sxeix.pokemanager.domain.service;


import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.model.IdempotencyRecord;
import com.sxeix.pokemanager.domain.repository.IdempotencyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    @Transactional
    public Optional<IdempotencyRecord> find(final String key) {

        return idempotencyRecordRepository.findById(key);
    }

    @Transactional
    public IdempotencyRecord create(final String key) {
        IdempotencyRecord idempotencyRecord = new IdempotencyRecord();
        idempotencyRecord.setIdempotencyKey(key);
        idempotencyRecord.setStatus(Status.PROCESSING);
        return idempotencyRecordRepository.saveAndFlush(idempotencyRecord);
    }

    @Transactional
    public void complete(final String key, final byte[] protobufBody) {
        idempotencyRecordRepository.findById(key).ifPresent(idempotencyRecord -> {
            idempotencyRecord.setStatus(Status.COMPLETED);
            idempotencyRecord.setResponseBody(protobufBody);
            idempotencyRecord.setUpdatedAt(LocalDateTime.now());
            idempotencyRecordRepository.saveAndFlush(idempotencyRecord);
        });
    }

    @Transactional
    public void complete(final String key) {
        idempotencyRecordRepository.findById(key).ifPresent(idempotencyRecord -> {
            idempotencyRecord.setStatus(Status.COMPLETED);
            idempotencyRecord.setUpdatedAt(LocalDateTime.now());
            idempotencyRecordRepository.saveAndFlush(idempotencyRecord);
        });
    }

    @Transactional
    public void fail(final String key) {
        idempotencyRecordRepository.findById(key).ifPresent(idempotencyRecord -> {
            idempotencyRecord.setStatus(Status.FAILED);
            idempotencyRecordRepository.saveAndFlush(idempotencyRecord);
        });
    }

}
