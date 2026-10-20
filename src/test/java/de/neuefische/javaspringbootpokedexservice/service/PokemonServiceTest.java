package de.neuefische.javaspringbootpokedexservice.service;

import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import de.neuefische.javaspringbootpokedexservice.exception.PokemonNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;

class PokemonServiceTest {

    @Test
    void getPokemonByName_shouldReturnMappedPokemon() {
        // GIVEN: Vorbereitung
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        PokemonService service = new PokemonService(builder);

        String responseBody = """
                {
                  "id": 35,
                  "name": "clefairy",
                  "height": 6,
                  "weight": 75,
                  "types": [
                    {
                      "type": {
                        "name": "fairy"
                      }
                    }
                  ],
                  "sprites": {
                    "other": {
                      "official-artwork": {
                        "front_default": "https://example.com/clefairy.png"
                      }
                    }
                  }
                }
                """;

        server.expect(requestTo(
                        "https://pokeapi.co/api/v2/pokemon/clefairy"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        responseBody, MediaType.APPLICATION_JSON));

        Pokemon expected = new Pokemon(
                null,
                "35",
                null,
                "clefairy",
                "https://example.com/clefairy.png",
                6,
                75,
                List.of("fairy")
        );

        // WHEN: Methode aufrufen
        Pokemon actual = service.getPokemonByName("clefairy");

        // THEN: Ergebnis prüfen
        assertEquals(expected, actual);
        server.verify();
    }


    @Test
    void getPokemonByName_shouldThrowException_whenPokemonDoesNotExist() {

        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        PokemonService service = new PokemonService(builder);

        server.expect(requestTo(
                        "https://pokeapi.co/api/v2/pokemon/unknownpokemon"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        assertThrows(
                PokemonNotFoundException.class,
                () -> service.getPokemonByName("unknownpokemon")
        );

        server.verify();
    }
}