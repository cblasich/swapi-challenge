package com.starwarsapp.integration.controller;

import com.starwarsapp.controllers.PeopleController;
import com.starwarsapp.dtos.PeopleDTO;
import com.starwarsapp.dtos.PeopleDetailDTO;
import com.starwarsapp.security.JwtAuthenticationFilter;
import com.starwarsapp.security.JwtUtil;
import com.starwarsapp.services.SwapiPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PeopleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PeopleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SwapiPeopleService swapiPeopleService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void getPeopleById_whenServiceThrowsException_returnsInternalServerError() throws Exception {
        when(swapiPeopleService.getOne("999")).thenThrow(new RuntimeException("Fallo SWAPI"));

        mockMvc.perform(get("/api/swapi/people/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("error inesperado")));
    }

    @Test
    void getPeopleById_resultOk() throws Exception {
        PeopleDetailDTO detail = new PeopleDetailDTO();
        detail.setUid("1");
        detail.setName("Luke Skywalker");

        when(swapiPeopleService.getOne("1")).thenReturn(detail);

        mockMvc.perform(get("/api/swapi/people/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("1"))
                .andExpect(jsonPath("$.name").value("Luke Skywalker"));
    }

    @Test
    @WithMockUser
    void getPeople_returnsListOfPeople() throws Exception {
        List<PeopleDTO> people = new ArrayList<>();
        PeopleDTO people1 = new PeopleDTO();
        people1.setUid("1");
        people1.setName("Luke Skywalker");
        people.add(people1);

        PeopleDTO people2 = new PeopleDTO();
        people2.setUid("4");
        people2.setName("Darth Vader");
        people.add(people2);

        when(swapiPeopleService.list(1, 10)).thenReturn(people);

        mockMvc.perform(get("/api/swapi/people/all?page=1&limit=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uid").value("1"))
                .andExpect(jsonPath("$[0].name").value("Luke Skywalker"))
                .andExpect(jsonPath("$[1].uid").value("4"))
                .andExpect(jsonPath("$[1].name").value("Darth Vader"));
    }
}
