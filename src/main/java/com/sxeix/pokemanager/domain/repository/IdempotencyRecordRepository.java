package com.sxeix.pokemanager.domain.repository;

import com.sxeix.pokemanager.domain.model.IdempotencyRecord;

import java.util.Optional;

public interface IdempotencyRecordRepository {

    IdempotencyRecord saveAndFlush(IdempotencyRecord idempotencyRecord);

    Optional<IdempotencyRecord> findById(String id);

}
