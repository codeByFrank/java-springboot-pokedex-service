package de.neuefische.javaspringbootpokedexservice.controller;

import de.neuefische.javaspringbootpokedexservice.exception.GlobalExceptionHandler;
import de.neuefische.javaspringbootpokedexservice.exception.PokemonNotFoundException;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.service.PokemonService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PokemonControllerTest {

    @Test
    void getPokemonByName_shouldReturn404_whenPokemonDoesNotExist()
            throws Exception {

        PokemonService service = mock(PokemonService.class);

        when(service.getPokemonByName("unknownpokemon"))
                .thenThrow(new PokemonNotFoundException(
                        "Pokemon 'unknownpokemon' not found"
                ));

        PokemonController controller = new PokemonController(service);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/pokemon/unknownpokemon"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Pokemon 'unknownpokemon' not found"
                ))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void getRandomPokemon_shouldReturnPokemon() throws Exception {
        // GIVEN
        PokemonService service = mock(PokemonService.class);

        Pokemon pokemon = new Pokemon(
                null,
                "92",
                null,
                "gastly",
                "https://example.com/gastly.png",
                13,
                1,
                List.of("ghost", "poison")
        );

        when(service.getRandomPokemon()).thenReturn(pokemon);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new PokemonController(service))
                .build();

        // WHEN + THEN
        mockMvc.perform(get("/api/pokemon/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pokemonId").value("92"))
                .andExpect(jsonPath("$.pokemonName").value("gastly"))
                .andExpect(jsonPath("$.types[0]").value("ghost"))
                .andExpect(jsonPath("$.types[1]").value("poison"));

        verify(service).getRandomPokemon();
    }
}