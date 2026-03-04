package com.sxeix.pokemanager.saga.events;

public record PokemonFetchRequest(
        int userId,
        int pokemonNum,
        String idempotencyKey
) {
}
