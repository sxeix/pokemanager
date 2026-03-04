package com.sxeix.pokemanager.infrastructure.pokeapi;

import java.util.Optional;

public interface PokeApiClient {

    Optional<String> fetchData(Integer pokemonNum); // TODO: return record
}
