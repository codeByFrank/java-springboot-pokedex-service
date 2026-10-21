package de.neuefische.javaspringbootpokedexservice.service;

import de.neuefische.javaspringbootpokedexservice.dto.PokemonSummaryDTO;
import de.neuefische.javaspringbootpokedexservice.exception.PokemonTypeNotFoundException;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import de.neuefische.javaspringbootpokedexservice.exception.PokemonNotFoundException;
import de.neuefische.javaspringbootpokedexservice.dto.PokemonComparisonDTO;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
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

    @Test
    void getRandomPokemon_shouldRequestIdBetween1And151() {
        // GIVEN
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        PokemonService service = new PokemonService(builder);

        String responseBody = """
            {
              "id": 92,
              "name": "gastly",
              "height": 13,
              "weight": 1,
              "types": [
                {"type": {"name": "ghost"}},
                {"type": {"name": "poison"}}
              ],
              "sprites": {
                "other": {
                  "official-artwork": {
                    "front_default": "https://example.com/gastly.png"
                  }
                }
              }
            }
            """;

        server.expect(request -> {
                    String path = request.getURI().getPath();

                    assertTrue(path.startsWith("/api/v2/pokemon/"));

                    String idText = path.substring(path.lastIndexOf("/") + 1);
                    int pokemonId = Integer.parseInt(idText);

                    assertTrue(pokemonId >= 1 && pokemonId <= 151);
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        responseBody, MediaType.APPLICATION_JSON));

        // WHEN
        Pokemon actual = service.getRandomPokemon();

        // THEN
        assertEquals("gastly", actual.pokemonName());
        server.verify();
    }

    @Test
    void comparePokemon_shouldCompareWeightAndHeight() {
        // GIVEN
        PokemonService service =
                spy(new PokemonService(RestClient.builder()));

        Pokemon firstPokemon = new Pokemon(
                null,
                "1",
                null,
                "first",
                "https://example.com/first.png",
                20,
                100,
                List.of("grass")
        );

        Pokemon secondPokemon = new Pokemon(
                null,
                "2",
                null,
                "second",
                "https://example.com/second.png",
                10,
                300,
                List.of("fire")
        );

        doReturn(firstPokemon)
                .when(service).getPokemonByName("first");

        doReturn(secondPokemon)
                .when(service).getPokemonByName("second");

        PokemonComparisonDTO expected = new PokemonComparisonDTO(
                "second",
                200,
                "first",
                10
        );

        // WHEN
        PokemonComparisonDTO actual =
                service.comparePokemon("first", "second");

        // THEN
        assertEquals(expected, actual);
        verify(service).getPokemonByName("first");
        verify(service).getPokemonByName("second");
    }

    @Test
    void comparePokemon_shouldReturnZeroDifferences_whenValuesAreEqual() {
        // GIVEN
        PokemonService service =
                spy(new PokemonService(RestClient.builder()));

        Pokemon first = new Pokemon(
                null, "1", null, "first",
                "https://example.com/first.png",
                10, 100, List.of("grass")
        );

        Pokemon second = new Pokemon(
                null, "2", null, "second",
                "https://example.com/second.png",
                10, 100, List.of("fire")
        );

        doReturn(first).when(service).getPokemonByName("first");
        doReturn(second).when(service).getPokemonByName("second");

        PokemonComparisonDTO expected =
                new PokemonComparisonDTO(null, 0, null, 0);

        // WHEN
        PokemonComparisonDTO actual =
                service.comparePokemon("first", "second");

        // THEN
        assertEquals(expected, actual);
        verify(service).getPokemonByName("first");
        verify(service).getPokemonByName("second");
    }

    @Test
    void getPokemonByType_shouldReturnSimplifiedPokemonList() {
        // GIVEN
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        PokemonService service = new PokemonService(builder);

        String responseBody = """
            {
              "pokemon": [
                {
                  "pokemon": {
                    "name": "charmander",
                    "url": "https://pokeapi.co/api/v2/pokemon/4/"
                  }
                },
                {
                  "pokemon": {
                    "name": "charmeleon",
                    "url": "https://pokeapi.co/api/v2/pokemon/5/"
                  }
                }
              ]
            }
            """;

        server.expect(requestTo(
                        "https://pokeapi.co/api/v2/type/fire"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        responseBody, MediaType.APPLICATION_JSON));

        List<PokemonSummaryDTO> expected = List.of(
                new PokemonSummaryDTO(
                        "charmander",
                        "https://pokeapi.co/api/v2/pokemon/4/"
                ),
                new PokemonSummaryDTO(
                        "charmeleon",
                        "https://pokeapi.co/api/v2/pokemon/5/"
                )
        );

        // WHEN
        List<PokemonSummaryDTO> actual = service.getPokemonByType("fire");

        // THEN
        assertEquals(expected, actual);
        server.verify();
    }

    @Test
    void getPokemonByType_shouldThrowException_whenTypeDoesNotExist() {
        // GIVEN
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        PokemonService service = new PokemonService(builder);

        server.expect(requestTo(
                        "https://pokeapi.co/api/v2/type/unknown-type"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        // WHEN + THEN
        assertThrows(
                PokemonTypeNotFoundException.class,
                () -> service.getPokemonByType("unknown-type")
        );

        server.verify();
    }
}