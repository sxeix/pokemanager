package com.sxeix.pokemanager.saga.listeners;

import com.sxeix.pokemanager.domain.events.PokemonAddFetchEvent;
import com.sxeix.pokemanager.domain.events.PokemonAddSaveErrorEvent;
import com.sxeix.pokemanager.domain.events.PokemonAddSaveRequestEvent;
import com.sxeix.pokemanager.domain.messaging.EventPublisher;
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
    private final EventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPokemonFetchRequest(final PokemonAddFetchEvent pokemonAddFetchEvent) {
        var pokemonData = pokeApiClient.fetchData(pokemonAddFetchEvent.pokemonNum());

        if (pokemonData.isPresent()) {
            eventPublisher.publish(new PokemonAddSaveRequestEvent(
                    pokemonAddFetchEvent.userId(),
                    pokemonAddFetchEvent.userPokemonId(),
                    pokemonAddFetchEvent.pokemonNum(),
                    pokemonData.get(),
                    pokemonAddFetchEvent.idempotencyKey()
            ));
        } else {
            eventPublisher.publish(new PokemonAddSaveErrorEvent(
                    pokemonAddFetchEvent.userId(),
                    pokemonAddFetchEvent.userPokemonId(),
                    pokemonAddFetchEvent.pokemonNum(),
                    "Pokemon details could not be fetched from API",
                    pokemonAddFetchEvent.idempotencyKey()
            ));
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPokemonSaveRequest(final PokemonAddSaveRequestEvent pokemonAddSaveRequestEvent) {
        userPokemonService.markSaving(pokemonAddSaveRequestEvent.userPokemonId());
        userPokemonService.updatePokemonDetails(
                pokemonAddSaveRequestEvent.userPokemonId(),
                pokemonAddSaveRequestEvent.pokemonDetails()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPokemonSaveError(final PokemonAddSaveErrorEvent pokemonAddSaveErrorEvent) {
        userPokemonService.markFailed(
                pokemonAddSaveErrorEvent.userPokemonId(),
                pokemonAddSaveErrorEvent.failureReason()
        );
    }
}
