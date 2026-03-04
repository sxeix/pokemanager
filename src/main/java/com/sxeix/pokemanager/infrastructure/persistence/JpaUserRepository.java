package com.sxeix.pokemanager.infrastructure.persistence;

import com.sxeix.pokemanager.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Integer> {
}
