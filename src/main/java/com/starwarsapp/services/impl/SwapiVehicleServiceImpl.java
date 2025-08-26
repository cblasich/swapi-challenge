package com.starwarsapp.services.impl;

import com.starwarsapp.client.SwapiClient;
import com.starwarsapp.dtos.VehicleDTO;
import com.starwarsapp.dtos.VehicleDetailDTO;
import com.starwarsapp.services.SwapiVehicleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SwapiVehicleServiceImpl implements SwapiVehicleService {

    private final SwapiClient swapiClient;

    public SwapiVehicleServiceImpl(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    @Override
    public List<VehicleDTO> list(int page, int limit) {
        return swapiClient.getVehicles(page, limit);
    }

    @Override
    public VehicleDetailDTO getOne(String id) {
        return swapiClient.getOneVehicle(id);
    }

    @Override
    public Integer getTotalRecords() {
        return swapiClient.getTotalVehiclesRecords();
    }
}
