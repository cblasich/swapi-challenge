package com.starwarsapp.controllers;

import com.starwarsapp.dtos.FilmDTO;
import com.starwarsapp.services.SwapiFilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/swapi/films")
public class FilmController {

    private final SwapiFilmService swapiFilmService;

    public FilmController(SwapiFilmService swapiFilmService) {
        this.swapiFilmService = swapiFilmService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<FilmDTO>> getFilms(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(swapiFilmService.list(page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDTO> getFilmById(@PathVariable("id") String id) {
        return ResponseEntity.ok(swapiFilmService.getOne(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterFilmByIdByTitle(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name) {

        // Si viene ID, devuelvo lo que coincida con el mismo ya que es unico
        if (id != null && !id.isEmpty()) {
            FilmDTO detail = swapiFilmService.getOne(id);
            return ResponseEntity.ok(detail);
        }

        // Filtrado por nombre si viene
        List<FilmDTO> filtered = new ArrayList<>();
        if (name != null) {
            List<FilmDTO> allFilms = swapiFilmService.list(1, swapiFilmService.getTotalRecords());
            filtered = allFilms.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(filtered);
    }
}
