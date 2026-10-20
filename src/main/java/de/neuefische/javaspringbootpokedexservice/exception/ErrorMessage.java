package de.neuefische.javaspringbootpokedexservice.exception;

import java.time.Instant;

public record ErrorMessage(String message, Instant timestamp) {
}
