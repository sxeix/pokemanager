package com.sxeix.pokemanager.domain.service;

import com.sxeix.pokemanager.AddPokemonRequest;
import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.events.PokemonAddFetchEvent;
import com.sxeix.pokemanager.domain.exception.UserNotFoundException;
import com.sxeix.pokemanager.domain.model.UserPokemon;
import com.sxeix.pokemanager.domain.messaging.EventPublisher;
import com.sxeix.pokemanager.domain.repository.UserPokemonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPokemonService {

    private final UserService userService;
    private final UserPokemonRepository userPokemonRepository;
    private final IdempotencyService idempotencyService;
    private final EventPublisher eventPublisher;

    @Transactional
    public void initiateAddPokemon(final AddPokemonRequest addPokemonRequest) throws UserNotFoundException { // TODO remove grpc stuff from touching anything in the domain

        if (userService.findUserById(addPokemonRequest.getUserId()).isEmpty()) {
            throw new UserNotFoundException("User id %s does not exist".formatted(addPokemonRequest.getUserId()));
        }

        var userReference = userService.getReferenceById(addPokemonRequest.getUserId());

        // TODO: add check to ensure no more than 6 pokemon

        var userPokemon = new UserPokemon();
        userPokemon.setUser(userReference);
        userPokemon.setPokemonNum(addPokemonRequest.getPokemonNum());
        userPokemon.setStatus(Status.PROCESSING);

        var savedUserPokemon = userPokemonRepository.save(userPokemon);

        idempotencyService.complete(addPokemonRequest.getIdempotencyKey());

        eventPublisher.publish(new PokemonAddFetchEvent(addPokemonRequest.getUserId(), savedUserPokemon.getId(), addPokemonRequest.getPokemonNum(), addPokemonRequest.getIdempotencyKey()));
    }

    @Transactional
    public void updatePokemonDetails(final Integer userPokemonId, final String pokemonDetails) {
        userPokemonRepository.findById(userPokemonId).map(userPokemon -> {
            userPokemon.setPokemonDetails(pokemonDetails);
            return userPokemonRepository.save(userPokemon);
        });
    }

}
