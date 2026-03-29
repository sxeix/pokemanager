package com.sxeix.pokemanager.infrastructure.pokeapi;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Service
public class PokeApiClientImpl implements PokeApiClient {

    private static final String BASE_URL = "https://pokeapi.co/api/v2/%s";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    @Override
    @CircuitBreaker(name = "pokeApi")
    @Retry(name = "pokeApi", fallbackMethod = "handleFailure")
    public Optional<String> fetchData(Integer pokemonNum) {

        var httpRequest = HttpRequest.newBuilder()
                .GET()
                .timeout(Duration.ofSeconds(20))
                .uri(
                    URI.create(BASE_URL.formatted("pokemon/%s".formatted(pokemonNum)))
                ).build();

        try {
            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return Optional.ofNullable(response.body());
            }

            if (response.statusCode() == 429 || response.statusCode() >= 500) {
                throw new PokeApiClientException("Retriable PokeAPI status: " + response.statusCode());
            }

            throw new PokeApiNonRetryableException("Non-retriable PokeAPI status: " + response.statusCode());
        } catch (IOException e) {
            throw new PokeApiClientException("I/O error while calling PokeAPI", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PokeApiClientException("Interrupted while calling PokeAPI", e);
        }
    }

    public Optional<String> handleFailure(Integer pokemonNum, Exception exception) {
        return Optional.empty();
    }


}
