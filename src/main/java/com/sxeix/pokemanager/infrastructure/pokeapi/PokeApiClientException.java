package com.sxeix.pokemanager.infrastructure.pokeapi;

public class PokeApiClientException extends RuntimeException {

    public PokeApiClientException(String message) {
        super(message);
    }

    public PokeApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
