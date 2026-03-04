package com.sxeix.pokemanager.domain.service;

import com.sxeix.pokemanager.AddPokemonRequest;
import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.exception.UserNotFoundException;
import com.sxeix.pokemanager.domain.model.UserPokemon;
import com.sxeix.pokemanager.domain.repository.UserPokemonRepository;
import com.sxeix.pokemanager.saga.events.PokemonFetchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPokemonService {

    private final UserService userService;
    private final UserPokemonRepository userPokemonPersistenceAdapter;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IdempotencyService idempotencyService;

    @Transactional
    public void initiateAddPokemon(final AddPokemonRequest addPokemonRequest) throws UserNotFoundException {

        if (userService.findUserById(addPokemonRequest.getUserId()).isEmpty()) {
            throw new UserNotFoundException("User id %s does not exist".formatted(addPokemonRequest.getUserId()));
        }

        var userReference = userService.getReferenceById(addPokemonRequest.getUserId());

        // TODO: add check to ensure no more than 6 pokemon

        var userPokemon = new UserPokemon();
        userPokemon.setUser(userReference);
        userPokemon.setPokemonNum(addPokemonRequest.getPokemonNum());
        userPokemon.setStatus(Status.STARTED);

        userPokemonPersistenceAdapter.save(userPokemon);

        idempotencyService.complete(addPokemonRequest.getIdempotencyKey());

        applicationEventPublisher.publishEvent(new PokemonFetchRequest(addPokemonRequest.getUserId(), addPokemonRequest.getPokemonNum(), addPokemonRequest.getIdempotencyKey()));
    }

}
