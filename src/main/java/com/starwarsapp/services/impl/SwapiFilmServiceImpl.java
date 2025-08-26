package com.starwarsapp.services.impl;

import com.starwarsapp.client.SwapiClient;
import com.starwarsapp.dtos.FilmDTO;
import com.starwarsapp.dtos.PeopleDTO;
import com.starwarsapp.dtos.PeopleDetailDTO;
import com.starwarsapp.services.SwapiFilmService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SwapiFilmServiceImpl implements SwapiFilmService {
    private final SwapiClient swapiClient;

    public SwapiFilmServiceImpl(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    @Override
    public List<FilmDTO> list(int page, int limit) {
        return swapiClient.getFilms(page, limit);
    }

    @Override
    public FilmDTO getOne(String id) {
        return swapiClient.getOneFilm(id);
    }

    @Override
    public Integer getTotalRecords() {
        return swapiClient.getTotalFilmsRecords();
    }
}
