package com.starwarsapp.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.starwarsapp.client.SwapiClient;
import com.starwarsapp.dtos.PeopleDTO;
import com.starwarsapp.dtos.PeopleDetailDTO;
import com.starwarsapp.services.SwapiPeopleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SwapiPeopleServiceImp implements SwapiPeopleService {

    private final SwapiClient swapiClient;

    public SwapiPeopleServiceImp(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    @Override
    public List<PeopleDTO> list(int page, int limit) {
        return swapiClient.getPeople(page, limit);
    }

    @Override
    public PeopleDetailDTO getOne(String id) {
        return swapiClient.getOnePeople(id);
    }

    @Override
    public Integer getTotalRecords() {
        return swapiClient.getTotalPeopleRecords();
    }

}
