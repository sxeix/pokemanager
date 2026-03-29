package com.sxeix.pokemanager.domain.repository;

import com.sxeix.pokemanager.domain.model.UserPokemon;

import java.util.Optional;

public interface UserPokemonRepository {

    Optional<UserPokemon> findById(Integer num);

    UserPokemon save(UserPokemon userPokemon);

    long countNonFailedByUserId(Integer userId);

}
