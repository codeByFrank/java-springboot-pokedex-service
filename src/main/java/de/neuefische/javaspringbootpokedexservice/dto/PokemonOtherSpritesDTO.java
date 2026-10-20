package de.neuefische.javaspringbootpokedexservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokemonOtherSpritesDTO(
        @JsonProperty("official-artwork") PokemonArtworkDTO officialArtwork
) {
}
