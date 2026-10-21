package de.neuefische.javaspringbootpokedexservice.exception;

public class PokemonTypeNotFoundException extends RuntimeException {
    public PokemonTypeNotFoundException(String message) {
        super(message);
    }
}
