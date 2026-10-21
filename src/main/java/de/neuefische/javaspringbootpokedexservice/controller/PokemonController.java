package de.neuefische.javaspringbootpokedexservice.controller;

import de.neuefische.javaspringbootpokedexservice.dto.PokemonComparisonDTO;
import de.neuefische.javaspringbootpokedexservice.dto.PokemonSummaryDTO;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.service.PokemonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/{name}")
    public Pokemon getPokemonByName(@PathVariable String name) {
        return pokemonService.getPokemonByName(name);
    }

    @GetMapping("/random")
    public Pokemon getRandomPokemon() {
        return pokemonService.getRandomPokemon();
    }

    @GetMapping("/compare/{pokemon1}/{pokemon2}")
    public PokemonComparisonDTO comparePokemon(
            @PathVariable String pokemon1,
            @PathVariable String pokemon2
    ) {
        return pokemonService.comparePokemon(pokemon1, pokemon2);
    }

    @GetMapping("/type/{type}")
    public List<PokemonSummaryDTO> getPokemonByType(
            @PathVariable String type
    ) {
        return pokemonService.getPokemonByType(type);
    }
}
