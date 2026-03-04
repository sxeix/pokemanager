package com.sxeix.pokemanager.infrastructure.pokeapi;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PokeApiClientImpl implements PokeApiClient {

    @Override
    @CircuitBreaker(name = "pokeApi")
    @Retry(name = "pokeApi", fallbackMethod = "handleFailure")
    public Optional<String> fetchData(Integer pokemonNum) {
        return Optional.of("success");
    }

    public Optional<String> handleFailure(Integer pokemonNum, Exception exception) {
        return Optional.empty();
    }


}
