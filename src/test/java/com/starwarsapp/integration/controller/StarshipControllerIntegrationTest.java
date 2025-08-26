package com.starwarsapp.integration.controller;

import com.starwarsapp.controllers.StarshipController;
import com.starwarsapp.dtos.StarshipDTO;
import com.starwarsapp.dtos.StarshipDetailDTO;
import com.starwarsapp.security.JwtAuthenticationFilter;
import com.starwarsapp.security.JwtUtil;
import com.starwarsapp.services.SwapiStarshipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(StarshipController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StarshipControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SwapiStarshipService swapiStarshipService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void getStarshipById_whenServiceThrowsException_returnsInternalServerError() throws Exception {
        when(swapiStarshipService.getOne("999")).thenThrow(new RuntimeException("Fallo SWAPI"));

        mockMvc.perform(get("/api/swapi/starships/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("error inesperado")));
    }

    @Test
    void getStarshipById_resultOk() throws Exception {
        StarshipDetailDTO detail = new StarshipDetailDTO();
        detail.setUid("48");
        detail.setName("Jedi starfighter");

        when(swapiStarshipService.getOne("48")).thenReturn(detail);

        mockMvc.perform(get("/api/swapi/starships/48")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("48"))
                .andExpect(jsonPath("$.name").value("Jedi starfighter"));
    }

    @Test
    @WithMockUser
    void getStarships_returnsListOfStarships() throws Exception {
        List<StarshipDTO> starships = new ArrayList<>();
        StarshipDTO starship1 = new StarshipDTO();
        starship1.setUid("3");
        starship1.setName("Star Destroyer");
        starships.add(starship1);

        StarshipDTO starship2 = new StarshipDTO();
        starship2.setUid("9");
        starship2.setName("Death Star");
        starships.add(starship2);

        when(swapiStarshipService.list(1, 10)).thenReturn(starships);

        mockMvc.perform(get("/api/swapi/starships/all?page=1&limit=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uid").value("3"))
                .andExpect(jsonPath("$[0].name").value("Star Destroyer"))
                .andExpect(jsonPath("$[1].uid").value("9"))
                .andExpect(jsonPath("$[1].name").value("Death Star"));
    }
}
