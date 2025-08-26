package com.starwarsapp.integration.controller;

import com.starwarsapp.dtos.FilmDTO;
import com.starwarsapp.services.SwapiFilmService;
import com.starwarsapp.controllers.FilmController;
import com.starwarsapp.security.JwtAuthenticationFilter;
import com.starwarsapp.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(FilmController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FilmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SwapiFilmService swapiFilmService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser // Simula un usuario autenticado
    void getFilmById_whenServiceThrowsException_returnsInternalServerError() throws Exception {
        when(swapiFilmService.getOne("999")).thenThrow(new RuntimeException("Fallo SWAPI"));

        mockMvc.perform(get("/api/swapi/films/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("error inesperado")));
    }

    @Test
    void getFilmById_resultOk() throws Exception {
        FilmDTO detail = new FilmDTO();
        detail.setUid("1");
        detail.setTitle("A New Hope");

        when(swapiFilmService.getOne("1")).thenReturn(detail);

        mockMvc.perform(get("/api/swapi/films/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("1"))
                .andExpect(jsonPath("$.title").value("A New Hope"));
    }

    @Test
    @WithMockUser
    void getFilms_returnsListOfFilms() throws Exception {
        List<FilmDTO> films = new ArrayList<>();
        FilmDTO film1 = new FilmDTO();
        film1.setUid("1");
        film1.setTitle("A New Hope");
        films.add(film1);

        FilmDTO film2 = new FilmDTO();
        film2.setUid("2");
        film2.setTitle("The Empire Strikes Back");
        films.add(film2);

        when(swapiFilmService.list(1, 10)).thenReturn(films);

        mockMvc.perform(get("/api/swapi/films/all?page=1&limit=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uid").value("1"))
                .andExpect(jsonPath("$[0].title").value("A New Hope"))
                .andExpect(jsonPath("$[1].uid").value("2"))
                .andExpect(jsonPath("$[1].title").value("The Empire Strikes Back"));
    }

}
