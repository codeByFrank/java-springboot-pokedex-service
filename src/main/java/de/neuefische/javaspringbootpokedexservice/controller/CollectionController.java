package de.neuefische.javaspringbootpokedexservice.controller;

import de.neuefische.javaspringbootpokedexservice.dto.FavoriteDTO;
import de.neuefische.javaspringbootpokedexservice.model.Pokemon;
import de.neuefische.javaspringbootpokedexservice.service.CollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pokemon addFavorite(@Valid @RequestBody FavoriteDTO favoriteDTO) {
        return collectionService.addFavorite(favoriteDTO);
    }

    @GetMapping
    public List<Pokemon> getAllFavorites() {
        return collectionService.getAllFavorites();
    }
}