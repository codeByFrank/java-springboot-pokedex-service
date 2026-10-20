package de.neuefische.javaspringbootpokedexservice.service;

import de.neuefische.javaspringbootpokedexservice.dto.FavoriteDTO;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.repository.PokemonRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollectionService {

    private final PokemonService pokemonService;
    private final PokemonRepository pokemonRepository;

    public CollectionService(
            PokemonService pokemonService,
            PokemonRepository pokemonRepository
    ) {
        this.pokemonService = pokemonService;
        this.pokemonRepository = pokemonRepository;
    }

    public Pokemon addFavorite(FavoriteDTO favoriteDTO) {
        Pokemon pokemon = pokemonService.getPokemonByName(
                favoriteDTO.pokemonName()
        );

        Pokemon favorite = new Pokemon(
                UUID.randomUUID().toString(),
                pokemon.pokemonId(),
                favoriteDTO.nickname(),
                pokemon.pokemonName(),
                pokemon.pictureUrl(),
                pokemon.height(),
                pokemon.weight(),
                pokemon.types()
        );

        return pokemonRepository.save(favorite);
    }
}
