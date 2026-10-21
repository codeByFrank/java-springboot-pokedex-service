package de.neuefische.javaspringbootpokedexservice.exception;

public class CollectionEntryNotFoundException extends RuntimeException {

    public CollectionEntryNotFoundException(String message) {
        super(message);
    }
}