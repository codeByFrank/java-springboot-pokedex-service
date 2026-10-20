package de.neuefische.javaspringbootpokedexservice.controller;

import de.neuefische.javaspringbootpokedexservice.dto.FavoriteDTO;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.service.CollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}