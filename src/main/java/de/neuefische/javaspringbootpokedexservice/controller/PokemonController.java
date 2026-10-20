package de.neuefische.javaspringbootpokedexservice.controller;

import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.service.PokemonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
