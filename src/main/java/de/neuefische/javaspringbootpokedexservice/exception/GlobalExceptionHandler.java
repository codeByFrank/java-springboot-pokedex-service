package de.neuefische.javaspringbootpokedexservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PokemonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handlePokemonNotFound(
            PokemonNotFoundException exception
    ) {
        return new ErrorMessage(
                exception.getMessage(),
                Instant.now()
        );
    }
}