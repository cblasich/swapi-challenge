package com.starwarsapp.integration.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starwarsapp.dtos.AuthRequestDTO;
import com.starwarsapp.dtos.UserRegisterDTO;
import org.junit.jupiter.api.Order;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/insert-roles.sql")
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void authenticateUser_returnsOk() throws Exception {
        UserRegisterDTO userDTO = new UserRegisterDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("testuser@example.com");
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        userDTO.setRoles(roles); // Agrega al menos un rol

        // Primero registro el usuario y luego lo voy a auntenticar
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"));

        // Autenticacion
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("testuser");
        authRequestDTO.setPassword("password123");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Order(2)
    void authenticateUser_returnsWrong() throws Exception {
        // Ya tengo usuario registrado del test anterior

        // Autenticacion
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("testuser");
        authRequestDTO.setPassword("passIncorrecta");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciales invalidas"));
    }

    // Test para acceso denegado por rol insuficiente
    @Test
    @Order(3)
    void accessProtectedEndpoint_withInsufficientRole_returnsForbidden() throws Exception {
        // registro de usuario con ROLE_PEOPLE
        UserRegisterDTO userDTO = new UserRegisterDTO();
        userDTO.setUsername("userConRolePeople");
        userDTO.setPassword("peoplePass123");
        userDTO.setEmail("testuserpeople@example.com");
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_PEOPLE");
        userDTO.setRoles(roles); // Agrega al menos un rol

        // Primero registro el usuario y luego lo voy a auntenticar
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("userConRolePeople"))
                .andExpect(jsonPath("$.email").value("testuserpeople@example.com"));


        // Autenticación con usuario que solo tiene ROLE_PEOPLE
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("userConRolePeople");
        authRequestDTO.setPassword("peoplePass123");

        // Obtener solo el token JWT del JSON de respuesta
        String response = mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        String token = jsonNode.get("token").asText();

        // Intentar acceder a endpoint solo para ROLE_FILMS
        // Usar el token limpio en el header
        mockMvc.perform(get("/api/swapi/films/all?page=1&limit=10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

}
