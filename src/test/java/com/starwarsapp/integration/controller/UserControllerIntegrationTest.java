package com.starwarsapp.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starwarsapp.dtos.UserRegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/insert-roles.sql")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_returnsCreatedAndUserResponse() throws Exception {
        UserRegisterDTO userDTO = new UserRegisterDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("testuser@example.com");
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        userDTO.setRoles(roles); // Agrega al menos un rol

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    void registerUser_withoutUsername_returnsBadRequest() throws Exception {
        UserRegisterDTO userDTO = new UserRegisterDTO();
        userDTO.setPassword("password123");
        userDTO.setEmail("testuser@example.com");
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        userDTO.setRoles(roles); // Incluye roles válidos

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
