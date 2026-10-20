package de.neuefische.javaspringbootpokedexservice.repository;

import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PokemonRepository
        extends MongoRepository<Pokemon, String> {
}