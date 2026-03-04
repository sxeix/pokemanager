package com.sxeix.pokemanager.infrastructure.persistence;

import com.sxeix.pokemanager.domain.model.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaIdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, String> {
}
