package com.sxeix.pokemanager.domain.service;

import com.sxeix.pokemanager.AddPokemonRequest;
import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.events.PokemonAddFetchEvent;
import com.sxeix.pokemanager.domain.exception.TeamFullException;
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

    private static final int MAX_POKEMON_PER_USER = 6;

    private final UserService userService;
    private final UserPokemonRepository userPokemonRepository;
    private final IdempotencyService idempotencyService;
    private final EventPublisher eventPublisher;

    @Transactional
    public void initiateAddPokemon(final AddPokemonRequest addPokemonRequest) throws UserNotFoundException, TeamFullException { // TODO remove grpc stuff from touching anything in the domain

        if (userService.findUserById(addPokemonRequest.getUserId()).isEmpty()) {
            throw new UserNotFoundException("User id %s does not exist".formatted(addPokemonRequest.getUserId()));
        }

        var userReference = userService.getReferenceById(addPokemonRequest.getUserId());

        if (userPokemonRepository.countNonFailedByUserId(addPokemonRequest.getUserId()) >= MAX_POKEMON_PER_USER) {
            throw new TeamFullException("User already has the maximum of %s Pokemon".formatted(MAX_POKEMON_PER_USER));
        }

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
            userPokemon.setStatus(Status.COMPLETED);
            userPokemon.setFailureReason(null);
            return userPokemonRepository.save(userPokemon);
        });
    }

    @Transactional
    public void markSaving(final Integer userPokemonId) {
        userPokemonRepository.findById(userPokemonId).ifPresent(userPokemon -> {
            userPokemon.setStatus(Status.SAVING);
            userPokemonRepository.save(userPokemon);
        });
    }

    @Transactional
    public void markFailed(final Integer userPokemonId, final String failureReason) {
        userPokemonRepository.findById(userPokemonId).ifPresent(userPokemon -> {
            userPokemon.setStatus(Status.FAILED);
            userPokemon.setFailureReason(failureReason);
            userPokemonRepository.save(userPokemon);
        });
    }

}
