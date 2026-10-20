package de.neuefische.javaspringbootpokedexservice.service;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import java.util.List;
import de.neuefische.javaspringbootpokedexservice.dto.PokemonApiDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PokemonService {

    private final RestClient restClient;

    public PokemonService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://pokeapi.co/api/v2")
                .build();
    }

    public Pokemon getPokemonByName(String name) {
        PokemonApiDTO pokemonApiDTO = restClient.get()
                .uri("/pokemon/{name}", name)
                .retrieve()
                .body(PokemonApiDTO.class);

        if (pokemonApiDTO == null) {
            throw new IllegalStateException("Pokemon API returned an empty response");
        }

        List<String> types = pokemonApiDTO.types()
                .stream()
                .map(type -> type.type().name())
                .toList();

        String pictureUrl = pokemonApiDTO.sprites()
                .other()
                .officialArtwork()
                .frontDefault();

        return new Pokemon(
                null,
                String.valueOf(pokemonApiDTO.id()),
                null,
                pokemonApiDTO.name(),
                pictureUrl,
                pokemonApiDTO.height(),
                pokemonApiDTO.weight(),
                types
        );
    }
}
