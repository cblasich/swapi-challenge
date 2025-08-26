package com.starwarsapp.services.impl;

import com.starwarsapp.client.SwapiClient;
import com.starwarsapp.dtos.StarshipDTO;
import com.starwarsapp.dtos.StarshipDetailDTO;
import com.starwarsapp.services.SwapiStarshipService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SwapiStarshipServiceImpl implements SwapiStarshipService {

    private final SwapiClient swapiClient;

    public SwapiStarshipServiceImpl(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    @Override
    public List<StarshipDTO> list(int page, int limit) {
        return swapiClient.getStarships(page, limit);
    }

    @Override
    public StarshipDetailDTO getOne(String id) {
        return swapiClient.getOneStarship(id);
    }

    @Override
    public Integer getTotalRecords() {
        return swapiClient.getTotalStarshipsRecords();
    }
}
