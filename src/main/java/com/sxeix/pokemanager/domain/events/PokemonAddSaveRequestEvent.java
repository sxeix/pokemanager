package com.sxeix.pokemanager.domain.events;

public record PokemonAddSaveRequestEvent(
        int userId,
        int userPokemonId,
        int pokemonNum,
        String pokemonDetails,
        String idempotencyKey
) implements Event {
}
