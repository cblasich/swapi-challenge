package com.starwarsapp.services;

import com.starwarsapp.dtos.*;

import java.util.List;

public interface SwapiService<T,TDetail> {

    List<T> list(int page, int limit);

    TDetail getOne(String id);

    Integer getTotalRecords();

}
