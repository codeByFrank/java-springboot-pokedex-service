package de.neuefische.javaspringbootpokedexservice.repository;

import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PokemonRepository
        extends MongoRepository<Pokemon, String> {

    List<Pokemon> findByPokemonNameContainingIgnoreCaseOrNicknameContainingIgnoreCase(
            String pokemonName,
            String nickname
    );
}

