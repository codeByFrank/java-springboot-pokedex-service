package de.neuefische.javaspringbootpokedexservice.service;
import de.neuefische.javaspringbootpokedexservice.dto.PokemonComparisonDTO;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import java.util.List;
import java.util.Random;

import de.neuefische.javaspringbootpokedexservice.dto.PokemonApiDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import de.neuefische.javaspringbootpokedexservice.exception.PokemonNotFoundException;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class PokemonService {

    private final RestClient restClient;

    public PokemonService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://pokeapi.co/api/v2")
                .build();
    }

    public Pokemon getPokemonByName(String name) {

        PokemonApiDTO pokemonApiDTO;

        try {
            pokemonApiDTO = restClient.get()
                    .uri("/pokemon/{name}", name)
                    .retrieve()
                    .body(PokemonApiDTO.class);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new PokemonNotFoundException(
                    "Pokemon '" + name + "' not found"
            );
        }

        if (pokemonApiDTO == null) {
            throw new IllegalStateException("Pokemon API returned an empty response");
        }

        List<String> types = pokemonApiDTO.types()
                .stream()
                .map(type -> type.type().name())
                .toList();

        String pictureUrl = pokemonApiDTO.sprites()
                .other()
                .officialArtwork()
                .frontDefault();

        return new Pokemon(
                null,
                String.valueOf(pokemonApiDTO.id()),
                null,
                pokemonApiDTO.name(),
                pictureUrl,
                pokemonApiDTO.height(),
                pokemonApiDTO.weight(),
                types
        );
    }

    public Pokemon getRandomPokemon() {
        Random random = new Random();
        int pokemonId = random.nextInt(151) + 1;

        return getPokemonByName(String.valueOf(pokemonId));
    }

    public PokemonComparisonDTO comparePokemon(
            String firstName,
            String secondName
    ) {
        Pokemon first = getPokemonByName(firstName);
        Pokemon second = getPokemonByName(secondName);

        String heavierPokemon = null;

        if (first.weight() > second.weight()) {
            heavierPokemon = first.pokemonName();
        } else if (second.weight() > first.weight()) {
            heavierPokemon = second.pokemonName();
        }

        String tallerPokemon = null;

        if (first.height() > second.height()) {
            tallerPokemon = first.pokemonName();
        } else if (second.height() > first.height()) {
            tallerPokemon = second.pokemonName();
        }

        int weightDifference = Math.abs(first.weight() - second.weight());
        int heightDifference = Math.abs(first.height() - second.height());

        return new PokemonComparisonDTO(
                heavierPokemon,
                weightDifference,
                tallerPokemon,
                heightDifference
        );
    }
}
