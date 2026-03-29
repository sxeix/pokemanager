package com.sxeix.pokemanager.infrastructure.persistence;

import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.model.UserPokemon;
import com.sxeix.pokemanager.domain.repository.UserPokemonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPokemonPersistenceAdapter implements UserPokemonRepository {

    private final JpaUserPokemonRepository jpaUserPokemonRepository;

    @Override
    public Optional<UserPokemon> findById(final Integer id) {
        return jpaUserPokemonRepository.findById(id);
    }

    @Override
    public UserPokemon save(final UserPokemon userPokemon) {
        return jpaUserPokemonRepository.save(userPokemon);
    }

    @Override
    public long countNonFailedByUserId(final Integer userId) {
        var statuses = EnumSet.allOf(Status.class);
        statuses.remove(Status.FAILED);
        return jpaUserPokemonRepository.countAllByUserIdAndStatus(userId, statuses);
    }

}
