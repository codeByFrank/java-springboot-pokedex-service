package de.neuefische.javaspringbootpokedexservice.dto;

public record PokemonComparisonDTO(
        String heavierPokemon,
        int weightDifference,
        String tallerPokemon,
        int heightDifference
) {
}