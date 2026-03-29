package com.sxeix.pokemanager.infrastructure.pokeapi;

public class PokeApiNonRetryableException extends RuntimeException {

    public PokeApiNonRetryableException(String message) {
        super(message);
    }
}
