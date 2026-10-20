package de.neuefische.javaspringbootpokedexservice.dto;

import java.util.List;

public record PokemonApiDTO(
        int id,
        String name,
        int height,
        int weight,
        List<PokemonTypeDTO> types
) {
}
