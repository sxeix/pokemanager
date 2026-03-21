package com.sxeix.pokemanager.domain.events;

public record PokemonAddFetchEvent(
        int userId,
        int userPokemonId,
        int pokemonNum,
        String idempotencyKey
) implements Event {
}
