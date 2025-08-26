package com.starwarsapp.controllers;

import com.starwarsapp.dtos.VehicleDTO;
import com.starwarsapp.dtos.VehicleDetailDTO;
import com.starwarsapp.services.SwapiVehicleService;
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
@RequestMapping("api/swapi/vehicles")
public class VehicleController {

    private final SwapiVehicleService swapiVehicleService;

    public VehicleController(SwapiVehicleService swapiVehicleService) {
        this.swapiVehicleService = swapiVehicleService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<VehicleDTO>> getVehicles(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(swapiVehicleService.list(page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDetailDTO> getVehicleById(@PathVariable("id") String id) {
        return ResponseEntity.ok(swapiVehicleService.getOne(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterVehicleByIdByName(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name) {

        // Si viene ID, devuelvo lo que coincida con el mismo ya que es unico
        if (id != null && !id.isEmpty()) {
            VehicleDetailDTO detail = swapiVehicleService.getOne(id);
            return ResponseEntity.ok(detail);
        }

        // Filtrado por nombre si viene
        List<VehicleDTO> filtered = new ArrayList<>();
        if (name != null) {
            List<VehicleDTO> allVehicles = swapiVehicleService.list(1, swapiVehicleService.getTotalRecords());
            filtered = allVehicles.stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(filtered);
    }
}
