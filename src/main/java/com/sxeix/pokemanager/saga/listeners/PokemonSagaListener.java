package com.sxeix.pokemanager.saga.listeners;

import com.sxeix.pokemanager.domain.events.PokemonAddFetchEvent;
import com.sxeix.pokemanager.domain.service.UserPokemonService;
import com.sxeix.pokemanager.infrastructure.pokeapi.PokeApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PokemonSagaListener {

    private final PokeApiClient pokeApiClient;
    private final UserPokemonService userPokemonService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPokemonFetchRequest(final PokemonAddFetchEvent pokemonAddFetchEvent) {
        var pokemonData = pokeApiClient.fetchData(pokemonAddFetchEvent.pokemonNum());

        if (pokemonData.isPresent()) {
            // publish save event request
        } else {
            // publish save error request
        }
    }
}
