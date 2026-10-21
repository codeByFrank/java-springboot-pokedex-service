package de.neuefische.javaspringbootpokedexservice.service;

import de.neuefische.javaspringbootpokedexservice.dto.FavoriteDTO;
import de.neuefische.javaspringbootpokedexservice.dto.UpdateFavoriteDTO;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.repository.PokemonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import de.neuefische.javaspringbootpokedexservice.exception.CollectionEntryNotFoundException;

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

    public List<Pokemon> getAllFavorites() {
        return pokemonRepository.findAll();
    }

    public Pokemon getFavoriteById(String id) {
        return pokemonRepository.findById(id)
                .orElseThrow(() -> new CollectionEntryNotFoundException(
                        "Favorite with id '" + id + "' not found"
                ));
    }

    public void deleteFavorite(String id) {
        if (!pokemonRepository.existsById(id)) {
            throw new CollectionEntryNotFoundException(
                    "Favorite with id '" + id + "' not found"
            );
        }

        pokemonRepository.deleteById(id);
    }

    public Pokemon updateFavorite(String id, UpdateFavoriteDTO updateDTO) {
        Pokemon existing = getFavoriteById(id);

        Pokemon updated = new Pokemon(
                existing.id(),
                existing.pokemonId(),
                updateDTO.nickname(),
                existing.pokemonName(),
                existing.pictureUrl(),
                existing.height(),
                existing.weight(),
                existing.types()
        );

        return pokemonRepository.save(updated);
    }
}
