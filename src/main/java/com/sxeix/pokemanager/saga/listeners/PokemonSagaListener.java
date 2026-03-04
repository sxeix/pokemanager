package com.sxeix.pokemanager.saga.listeners;

import com.sxeix.pokemanager.infrastructure.pokeapi.PokeApiClient;
import com.sxeix.pokemanager.saga.events.PokemonFetchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PokemonSagaListener {

    private final PokeApiClient pokeApiClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPokemonFetchRequest(final PokemonFetchRequest pokemonFetchRequest) {
        var pokemonData = pokeApiClient.fetchData(pokemonFetchRequest.pokemonNum());

        if (pokemonData.isPresent()) {
            // publish save event request
        } else {
            // publish save error request
        }
    }
}
