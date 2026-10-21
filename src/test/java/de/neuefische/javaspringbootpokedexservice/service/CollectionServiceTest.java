package de.neuefische.javaspringbootpokedexservice.service;

import de.neuefische.javaspringbootpokedexservice.dto.FavoriteDTO;
import de.neuefische.javaspringbootpokedexservice.exception.CollectionEntryNotFoundException;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.repository.PokemonRepository;
import org.junit.jupiter.api.Test;
import de.neuefische.javaspringbootpokedexservice.exception.PokemonNotFoundException;

import java.util.List;
import java.util.Optional;
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

    @Test
    void getAllFavorites_shouldReturnSavedFavorites() {

        PokemonService pokemonService = mock(PokemonService.class);
        PokemonRepository pokemonRepository = mock(PokemonRepository.class);

        CollectionService collectionService =
                new CollectionService(pokemonService, pokemonRepository);

        Pokemon favorite = new Pokemon(
                "favorite-1",
                "25",
                "Mein Starter",
                "pikachu",
                "https://example.com/pikachu.png",
                4,
                60,
                List.of("electric")
        );

        List<Pokemon> expected = List.of(favorite);

        when(pokemonRepository.findAll()).thenReturn(expected);


        List<Pokemon> actual = collectionService.getAllFavorites();


        assertEquals(expected, actual);
        verify(pokemonRepository).findAll();
        verifyNoInteractions(pokemonService);
    }

    @Test
    void getFavoriteById_shouldReturnFavorite_whenIdExists() {

        PokemonService pokemonService = mock(PokemonService.class);
        PokemonRepository pokemonRepository = mock(PokemonRepository.class);

        CollectionService collectionService =
                new CollectionService(pokemonService, pokemonRepository);

        Pokemon favorite = new Pokemon(
                "favorite-1",
                "25",
                "Mein Starter",
                "pikachu",
                "https://example.com/pikachu.png",
                4,
                60,
                List.of("electric")
        );

        when(pokemonRepository.findById("favorite-1"))
                .thenReturn(Optional.of(favorite));


        Pokemon actual = collectionService.getFavoriteById("favorite-1");


        assertEquals(favorite, actual);
        verify(pokemonRepository).findById("favorite-1");
        verifyNoInteractions(pokemonService);
    }

    @Test
    void getFavoriteById_shouldThrowException_whenIdDoesNotExist() {
       
        PokemonService pokemonService = mock(PokemonService.class);
        PokemonRepository pokemonRepository = mock(PokemonRepository.class);

        CollectionService collectionService =
                new CollectionService(pokemonService, pokemonRepository);

        when(pokemonRepository.findById("unknown-id"))
                .thenReturn(Optional.empty());


        assertThrows(
                CollectionEntryNotFoundException.class,
                () -> collectionService.getFavoriteById("unknown-id")
        );

        verify(pokemonRepository).findById("unknown-id");
        verifyNoInteractions(pokemonService);
    }

    @Test
    void deleteFavorite_shouldDeleteFavorite_whenIdExists() {
        // GIVEN
        PokemonService pokemonService = mock(PokemonService.class);
        PokemonRepository pokemonRepository = mock(PokemonRepository.class);

        CollectionService collectionService =
                new CollectionService(pokemonService, pokemonRepository);

        when(pokemonRepository.existsById("favorite-1"))
                .thenReturn(true);

        // WHEN
        collectionService.deleteFavorite("favorite-1");

        // THEN
        verify(pokemonRepository).existsById("favorite-1");
        verify(pokemonRepository).deleteById("favorite-1");
        verifyNoInteractions(pokemonService);
    }

    @Test
    void deleteFavorite_shouldThrowException_whenIdDoesNotExist() {
        // GIVEN
        PokemonService pokemonService = mock(PokemonService.class);
        PokemonRepository pokemonRepository = mock(PokemonRepository.class);

        CollectionService collectionService =
                new CollectionService(pokemonService, pokemonRepository);

        when(pokemonRepository.existsById("unknown-id"))
                .thenReturn(false);

        // WHEN + THEN
        assertThrows(
                CollectionEntryNotFoundException.class,
                () -> collectionService.deleteFavorite("unknown-id")
        );

        verify(pokemonRepository, never()).deleteById(anyString());
        verifyNoInteractions(pokemonService);
    }
}