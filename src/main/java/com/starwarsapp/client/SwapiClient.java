package com.starwarsapp.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starwarsapp.dtos.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class used to consume the Swapi API
 */
@Component
public class SwapiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "https://www.swapi.tech/api";  // API de Star Wars

    public SwapiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private String buildUrl(String endpoint, Integer page, Integer limit, String id) {
        if (id != null) {
            return String.format("%s/%s/%s", BASE_URL, endpoint, id);
        }
        return String.format("%s/%s?page=%d&limit=%d", BASE_URL, endpoint, page, limit);
    }

    /**
     * Generic method to fetch a list of resources from SWAPI
     * @param endpoint API endpoint (e.g., "people", "films")
     * @param page
     * @param limit
     * @param clazz Class of the DTO to map the results to
     * @param fromProperties if true, maps from the "properties" field inside "result"
     * @return List of DTOs
     * @param <T>
     */
    private <T> List<T> fetchList(String endpoint, int page, int limit, Class<T> clazz, boolean fromProperties) {
        String url = buildUrl(endpoint, page, limit, null);
        String response = restTemplate.getForObject(url, String.class);

        try {
            if (fromProperties) {
                JavaType type = objectMapper.getTypeFactory().constructParametricType(SwapiResponseDTO.class, Object.class);
                SwapiResponseDTO<Object> swapiResponse = objectMapper.readValue(response, type);
                List<Object> results = swapiResponse.getResult();
                if (!results.isEmpty() && results.get(0) instanceof Map) {
                    return results.stream()
                            .map(r -> (Map<?, ?>) r)
                            .map(m -> {
                                Map<String, Object> props = (Map<String, Object>) m.get("properties");
                                props.put("uid", m.get("uid"));
                                return objectMapper.convertValue(props, clazz);
                            })
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();
            } else {
                JavaType type = objectMapper.getTypeFactory().constructParametricType(SwapiResponseDTO.class, clazz);
                SwapiResponseDTO<T> swapiResponse = objectMapper.readValue(response, type);
                return swapiResponse.getResults();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SWAPI response", e);
        }
    }

    public List<PeopleDTO> getPeople(int page, int limit) {
        return fetchList("people", page, limit, PeopleDTO.class, false);
    }

    public List<FilmDTO> getFilms(int page, int limit) {
        return fetchList("films", page, limit, FilmDTO.class, true);
    }

    public List<StarshipDTO> getStarships(int page, int limit) {
        return fetchList("starships", page, limit, StarshipDTO.class, false);
    }

    public List<VehicleDTO> getVehicles(int page, int limit) {
        return fetchList("vehicles", page, limit, VehicleDTO.class, false);
    }

    /**
     * Generic method to fetch a single resource by ID from SWAPI
     * @param endpoint API endpoint (e.g., "people", "films")
     * @param id ID of the resource
     * @param clazz Class of the DTO to map the result to
     * @return DTO
     * @param <T>
     */
    private <T> T fetchOne(String endpoint, String id, Class<T> clazz) {
        String url = buildUrl(endpoint, null, null, id);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode resultNode = root.path("result");
            JsonNode propertiesNode = resultNode.path("properties");
            T dto = objectMapper.treeToValue(propertiesNode, clazz);
            if (dto instanceof HasUid) {
                String uid = resultNode.path("uid").asText();
                ((HasUid) dto).setUid(uid);
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching resource from SWAPI: " + url, e);
        }
    }

    public PeopleDetailDTO getOnePeople(String id) {
        return fetchOne("people", id, PeopleDetailDTO.class);
    }

    public VehicleDetailDTO getOneVehicle(String id) {
        return fetchOne("vehicles", id, VehicleDetailDTO.class);
    }

    public StarshipDetailDTO getOneStarship(String id) {
        return fetchOne("starships", id, StarshipDetailDTO.class);
    }

    public FilmDTO getOneFilm(String id) {
        return fetchOne("films", id, FilmDTO.class);
    }

    public Integer getTotalPeopleRecords() {
        return getTotalRecords("people");
    }

    public Integer getTotalFilmsRecords() {
        return getTotalRecords("films");
    }

    public Integer getTotalVehiclesRecords() {
        return getTotalRecords("vehicles");
    }

    public Integer getTotalStarshipsRecords() {
        return getTotalRecords("starships");
    }

    public Integer getTotalRecords(String endpoint) {
        String url = buildUrl(endpoint, 1, 1, null);
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            return root.path("total_records").asInt();
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo total_records de SWAPI", e);
        }
    }

}
