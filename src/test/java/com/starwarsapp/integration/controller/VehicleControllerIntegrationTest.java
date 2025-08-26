package com.starwarsapp.integration.controller;

import com.starwarsapp.controllers.VehicleController;
import com.starwarsapp.dtos.VehicleDTO;
import com.starwarsapp.dtos.VehicleDetailDTO;
import com.starwarsapp.security.JwtAuthenticationFilter;
import com.starwarsapp.security.JwtUtil;
import com.starwarsapp.services.SwapiVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SwapiVehicleService swapiVehicleService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void getVehicleById_whenServiceThrowsException_returnsInternalServerError() throws Exception {
        when(swapiVehicleService.getOne("999")).thenThrow(new RuntimeException("Fallo SWAPI"));

        mockMvc.perform(get("/api/swapi/vehicles/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("error inesperado")));
    }

    @Test
    void getVehicleId_resultOk() throws Exception {
        VehicleDetailDTO detail = new VehicleDetailDTO();
        detail.setUid("4");
        detail.setName("Sand Crawler");

        when(swapiVehicleService.getOne("4")).thenReturn(detail);

        mockMvc.perform(get("/api/swapi/vehicles/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("4"))
                .andExpect(jsonPath("$.name").value("Sand Crawler"));
    }

    @Test
    @WithMockUser
    void getVehicles_returnsListOfVehicles() throws Exception {
        List<VehicleDTO> vehicles = new ArrayList<>();
        VehicleDTO vehicle1 = new VehicleDTO();
        vehicle1.setUid("4");
        vehicle1.setName("Sand Crawler");
        vehicles.add(vehicle1);

        VehicleDTO vehicle2 = new VehicleDTO();
        vehicle2.setUid("14");
        vehicle2.setName("Snowspeeder");
        vehicles.add(vehicle2);

        when(swapiVehicleService.list(1, 10)).thenReturn(vehicles);

        mockMvc.perform(get("/api/swapi/vehicles/all?page=1&limit=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uid").value("4"))
                .andExpect(jsonPath("$[0].name").value("Sand Crawler"))
                .andExpect(jsonPath("$[1].uid").value("14"))
                .andExpect(jsonPath("$[1].name").value("Snowspeeder"));
    }
}
