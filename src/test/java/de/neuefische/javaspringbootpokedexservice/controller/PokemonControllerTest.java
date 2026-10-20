package de.neuefische.javaspringbootpokedexservice.controller;

import de.neuefische.javaspringbootpokedexservice.exception.GlobalExceptionHandler;
import de.neuefische.javaspringbootpokedexservice.exception.PokemonNotFoundException;
import de.neuefische.javaspringbootpokedexservice.service.PokemonService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
}