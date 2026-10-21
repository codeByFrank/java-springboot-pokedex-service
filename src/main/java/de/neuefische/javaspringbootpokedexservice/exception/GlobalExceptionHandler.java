package de.neuefische.javaspringbootpokedexservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.stream.Collectors;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return new ErrorMessage(message, Instant.now());
    }

    @ExceptionHandler(CollectionEntryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleCollectionEntryNotFound(
            CollectionEntryNotFoundException exception
    ) {
        return new ErrorMessage(
                exception.getMessage(),
                Instant.now()
        );
    }

    @ExceptionHandler(PokemonTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handlePokemonTypeNotFound(
            PokemonTypeNotFoundException exception
    ) {
        return new ErrorMessage(
                exception.getMessage(),
                Instant.now()
        );
    }
}