package de.neuefische.javaspringbootpokedexservice.model;

import java.util.List;

public record Pokemon(
        String id,
        String pokemonId,
        String nickname,
        String pokemonName,
        String pictureUrl,
        int height,
        int weight,
        List<String> types
) {
}
