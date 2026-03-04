package com.sxeix.pokemanager.infrastructure.persistence;

import com.sxeix.pokemanager.domain.model.UserPokemon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserPokemonRepository extends JpaRepository<UserPokemon, Integer> {

}
