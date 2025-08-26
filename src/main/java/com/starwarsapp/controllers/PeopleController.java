package com.starwarsapp.controllers;

import com.starwarsapp.dtos.PeopleDTO;
import com.starwarsapp.dtos.PeopleDetailDTO;
import com.starwarsapp.services.SwapiPeopleService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/swapi/people")
public class PeopleController {

    private final SwapiPeopleService swapiPeopleService;

    public PeopleController(SwapiPeopleService swapiPeopleService) {
        this.swapiPeopleService = swapiPeopleService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PeopleDTO>> getPeople(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(swapiPeopleService.list(page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeopleDetailDTO> getPeopleById(@PathVariable("id") String id) {
        return ResponseEntity.ok(swapiPeopleService.getOne(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterPeopleByIdByName(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name) {

        // Si viene ID, devuelvo lo que coincida con el mismo ya que es unico
        if (id != null && !id.isEmpty()) {
            PeopleDetailDTO detail = swapiPeopleService.getOne(id);
            return ResponseEntity.ok(detail);
        }

        // Filtrado por nombre si viene
        List<PeopleDTO> filtered = new ArrayList<>();
        if (name != null) {
            List<PeopleDTO> allPeople = swapiPeopleService.list(1, swapiPeopleService.getTotalRecords());
            filtered = allPeople.stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(filtered);
    }

}
