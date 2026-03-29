package com.sxeix.pokemanager.domain.events;

public record PokemonAddSaveErrorEvent(
        int userId,
        int userPokemonId,
        int pokemonNum,
        String failureReason,
        String idempotencyKey
) implements Event {
}
