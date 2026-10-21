package de.neuefische.javaspringbootpokedexservice.controller;

import de.neuefische.javaspringbootpokedexservice.dto.FavoriteDTO;
import de.neuefische.javaspringbootpokedexservice.exception.CollectionEntryNotFoundException;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.service.CollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import de.neuefische.javaspringbootpokedexservice.exception.GlobalExceptionHandler;
import java.util.List;
import de.neuefische.javaspringbootpokedexservice.dto.UpdateFavoriteDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CollectionControllerTest {

    @Test
    void addFavorite_shouldReturn201AndSavedFavorite() throws Exception {

        CollectionService service = mock(CollectionService.class);

        FavoriteDTO favoriteDTO =
                new FavoriteDTO("pikachu", "Mein Starter");

        Pokemon favorite = new Pokemon(
                "e76e3a9e-2dc6-4905-a197-fe130b34093c",
                "25",
                "Mein Starter",
                "pikachu",
                "https://example.com/pikachu.png",
                4,
                60,
                List.of("electric")
        );

        when(service.addFavorite(favoriteDTO)).thenReturn(favorite);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .build();

        String requestBody = """
                {
                  "pokemonName": "pikachu",
                  "nickname": "Mein Starter"
                }
                """;

        mockMvc.perform(post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(favorite.id()))
                .andExpect(jsonPath("$.pokemonId").value("25"))
                .andExpect(jsonPath("$.pokemonName").value("pikachu"))
                .andExpect(jsonPath("$.nickname").value("Mein Starter"));

        verify(service).addFavorite(favoriteDTO);
    }

    @Test
    void addFavorite_shouldReturn400_whenNicknameIsTooShort() throws Exception {

        CollectionService service = mock(CollectionService.class);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String requestBody = """
            {
              "pokemonName": "pikachu",
              "nickname": "A"
            }
            """;

        mockMvc.perform(post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Nickname must contain at least 2 characters"
                ))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verifyNoInteractions(service);
    }

    @Test
    void addFavorite_shouldReturn400_whenFieldsAreEmpty() throws Exception {

        CollectionService service = mock(CollectionService.class);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String requestBody = """
            {
              "pokemonName": "",
              "nickname": ""
            }
            """;

        mockMvc.perform(post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString(
                                "Pokemon name must not be blank"
                        )
                ))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString(
                                "Nickname must not be blank"
                        )
                ));

        verifyNoInteractions(service);
    }

    @Test
    void getAllFavorites_shouldReturnSavedFavorites() throws Exception {

        CollectionService service = mock(CollectionService.class);

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

        when(service.getAllFavorites()).thenReturn(List.of(favorite));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .build();


        mockMvc.perform(get("/api/collection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("favorite-1"))
                .andExpect(jsonPath("$[0].pokemonName").value("pikachu"))
                .andExpect(jsonPath("$[0].nickname").value("Mein Starter"));

        verify(service).getAllFavorites();
    }

    @Test
    void getFavoriteById_shouldReturnFavorite_whenIdExists() throws Exception {

        CollectionService service = mock(CollectionService.class);

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

        when(service.getFavoriteById("favorite-1")).thenReturn(favorite);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .build();

        mockMvc.perform(get("/api/collection/favorite-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("favorite-1"))
                .andExpect(jsonPath("$.pokemonName").value("pikachu"))
                .andExpect(jsonPath("$.nickname").value("Mein Starter"));

        verify(service).getFavoriteById("favorite-1");
    }

    @Test
    void getFavoriteById_shouldReturn404_whenIdDoesNotExist() throws Exception {

        CollectionService service = mock(CollectionService.class);

        when(service.getFavoriteById("unknown-id"))
                .thenThrow(new CollectionEntryNotFoundException(
                        "Favorite with id 'unknown-id' not found"
                ));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();


        mockMvc.perform(get("/api/collection/unknown-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Favorite with id 'unknown-id' not found"
                ))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).getFavoriteById("unknown-id");
    }

    @Test
    void deleteFavorite_shouldReturn204_whenIdExists() throws Exception {
        // GIVEN
        CollectionService service = mock(CollectionService.class);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .build();

        // WHEN + THEN
        mockMvc.perform(delete("/api/collection/favorite-1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).deleteFavorite("favorite-1");
    }

    @Test
    void deleteFavorite_shouldReturn404_whenIdDoesNotExist() throws Exception {
        // GIVEN
        CollectionService service = mock(CollectionService.class);

        doThrow(new CollectionEntryNotFoundException(
                "Favorite with id 'unknown-id' not found"
        )).when(service).deleteFavorite("unknown-id");

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // WHEN + THEN
        mockMvc.perform(delete("/api/collection/unknown-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Favorite with id 'unknown-id' not found"
                ))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).deleteFavorite("unknown-id");
    }

    @Test
    void updateFavorite_shouldReturnUpdatedFavorite() throws Exception {
        // GIVEN
        CollectionService service = mock(CollectionService.class);

        UpdateFavoriteDTO updateDTO =
                new UpdateFavoriteDTO("Arena-Champion");

        Pokemon updated = new Pokemon(
                "favorite-1",
                "25",
                "Arena-Champion",
                "pikachu",
                "https://example.com/pikachu.png",
                4,
                60,
                List.of("electric")
        );

        when(service.updateFavorite("favorite-1", updateDTO))
                .thenReturn(updated);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .build();

        String requestBody = """
            {
              "nickname": "Arena-Champion"
            }
            """;

        // WHEN + THEN
        mockMvc.perform(put("/api/collection/favorite-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("favorite-1"))
                .andExpect(jsonPath("$.nickname").value("Arena-Champion"))
                .andExpect(jsonPath("$.pokemonName").value("pikachu"));

        verify(service).updateFavorite("favorite-1", updateDTO);
    }

    @Test
    void updateFavorite_shouldReturn400_whenNicknameIsTooShort() throws Exception {
        // GIVEN
        CollectionService service = mock(CollectionService.class);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String requestBody = """
            {
              "nickname": "A"
            }
            """;

        // WHEN + THEN
        mockMvc.perform(put("/api/collection/favorite-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Nickname must contain at least 2 characters"
                ))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verifyNoInteractions(service);
    }

    @Test
    void updateFavorite_shouldReturn404_whenIdDoesNotExist() throws Exception {
        // GIVEN
        CollectionService service = mock(CollectionService.class);

        UpdateFavoriteDTO updateDTO =
                new UpdateFavoriteDTO("Arena-Champion");

        when(service.updateFavorite("unknown-id", updateDTO))
                .thenThrow(new CollectionEntryNotFoundException(
                        "Favorite with id 'unknown-id' not found"
                ));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String requestBody = """
            {
              "nickname": "Arena-Champion"
            }
            """;

        // WHEN + THEN
        mockMvc.perform(put("/api/collection/unknown-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Favorite with id 'unknown-id' not found"
                ))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).updateFavorite("unknown-id", updateDTO);
    }

    @Test
    void searchFavorites_shouldReturnMatchingFavorites() throws Exception {
        // GIVEN
        CollectionService service = mock(CollectionService.class);

        Pokemon favorite = new Pokemon(
                "favorite-1",
                "25",
                "Arena-Champion",
                "pikachu",
                "https://example.com/pikachu.png",
                4,
                60,
                List.of("electric")
        );

        when(service.searchFavorites("pika"))
                .thenReturn(List.of(favorite));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .build();

        // WHEN + THEN
        mockMvc.perform(get("/api/collection/search")
                        .param("name", "pika"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("favorite-1"))
                .andExpect(jsonPath("$[0].pokemonName").value("pikachu"))
                .andExpect(jsonPath("$[0].nickname").value("Arena-Champion"));

        verify(service).searchFavorites("pika");
    }

    @Test
    void searchFavorites_shouldReturnEmptyList_whenNothingMatches()
            throws Exception {

        // GIVEN
        CollectionService service = mock(CollectionService.class);

        when(service.searchFavorites("xyz"))
                .thenReturn(List.of());

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CollectionController(service))
                .build();

        // WHEN + THEN
        mockMvc.perform(get("/api/collection/search")
                        .param("name", "xyz"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service).searchFavorites("xyz");
    }
}