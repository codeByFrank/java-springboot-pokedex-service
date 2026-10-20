package de.neuefische.javaspringbootpokedexservice.service;

import de.neuefische.javaspringbootpokedexservice.dto.FavoriteDTO;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.repository.PokemonRepository;
import org.junit.jupiter.api.Test;
import de.neuefische.javaspringbootpokedexservice.exception.PokemonNotFoundException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CollectionServiceTest {

    @Test
    void addFavorite_shouldSavePokemonWithNicknameAndGeneratedId() {

        PokemonService pokemonService = mock(PokemonService.class);
        PokemonRepository pokemonRepository = mock(PokemonRepository.class);

        CollectionService collectionService =
                new CollectionService(pokemonService, pokemonRepository);

        FavoriteDTO favoriteDTO =
                new FavoriteDTO("pikachu", "Mein Starter");

        Pokemon apiPokemon = new Pokemon(
                null,
                "25",
                null,
                "pikachu",
                "https://example.com/pikachu.png",
                4,
                60,
                List.of("electric")
        );

        when(pokemonService.getPokemonByName("pikachu"))
                .thenReturn(apiPokemon);

        when(pokemonRepository.save(any(Pokemon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pokemon actual = collectionService.addFavorite(favoriteDTO);

        assertNotNull(actual.id());
        assertDoesNotThrow(() -> UUID.fromString(actual.id()));

        Pokemon expected = new Pokemon(
                actual.id(),
                "25",
                "Mein Starter",
                "pikachu",
                "https://example.com/pikachu.png",
                4,
                60,
                List.of("electric")
        );

        assertEquals(expected, actual);
        verify(pokemonRepository).save(expected);
    }

    @Test
    void addFavorite_shouldNotSave_whenPokemonDoesNotExist() {

        PokemonService pokemonService = mock(PokemonService.class);
        PokemonRepository pokemonRepository = mock(PokemonRepository.class);

        CollectionService collectionService =
                new CollectionService(pokemonService, pokemonRepository);

        FavoriteDTO favoriteDTO =
                new FavoriteDTO("unknownpokemon", "Mein Starter");

        when(pokemonService.getPokemonByName("unknownpokemon"))
                .thenThrow(new PokemonNotFoundException(
                        "Pokemon 'unknownpokemon' not found"
                ));

        assertThrows(
                PokemonNotFoundException.class,
                () -> collectionService.addFavorite(favoriteDTO)
        );

        verify(pokemonRepository, never()).save(any(Pokemon.class));
    }
}