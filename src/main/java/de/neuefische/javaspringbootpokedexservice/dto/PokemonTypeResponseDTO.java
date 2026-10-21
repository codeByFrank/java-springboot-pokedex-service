package de.neuefische.javaspringbootpokedexservice.dto;

import java.util.List;

public record PokemonTypeResponseDTO(
        List<PokemonTypeEntryDTO> pokemon
) {
}