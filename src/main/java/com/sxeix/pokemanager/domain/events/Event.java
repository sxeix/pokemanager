package com.sxeix.pokemanager.domain.events;

public interface Event {
    int userId();
    int pokemonNum();
    String idempotencyKey();
}
