package com.starwarsapp.controllers;

import com.starwarsapp.dtos.StarshipDTO;
import com.starwarsapp.dtos.StarshipDetailDTO;
import com.starwarsapp.services.SwapiStarshipService;
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
@RequestMapping("api/swapi/starships")
public class StarshipController {

    private final SwapiStarshipService swapiStarshipService;

    public StarshipController(SwapiStarshipService swapiStarshipService) {
        this.swapiStarshipService = swapiStarshipService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<StarshipDTO>> getStarships(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(swapiStarshipService.list(page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StarshipDetailDTO> getStarshipById(@PathVariable("id") String id) {
        return ResponseEntity.ok(swapiStarshipService.getOne(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterStarshipByIdByName(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name) {

        // Si viene ID, devuelvo lo que coincida con el mismo ya que es unico
        if (id != null && !id.isEmpty()) {
            StarshipDetailDTO detail = swapiStarshipService.getOne(id);
            return ResponseEntity.ok(detail);
        }

        // Filtrado por nombre si viene
        List<StarshipDTO> filtered = new ArrayList<>();
        if (name != null) {
            List<StarshipDTO> allStarships = swapiStarshipService.list(1, swapiStarshipService.getTotalRecords());
            filtered = allStarships.stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(filtered);
    }
}
