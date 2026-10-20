package de.neuefische.javaspringbootpokedexservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokemonArtworkDTO(@JsonProperty("front_default") String frontDefault) {
}
