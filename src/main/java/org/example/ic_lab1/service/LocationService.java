package org.example.ic_lab1.service;

import org.example.ic_lab1.domain.Location;
import org.example.ic_lab1.dto.LocationDTO;

import java.util.List;

public interface LocationService {
    LocationDTO createLocation(LocationDTO location);
    LocationDTO updateLocation(LocationDTO locationDTO);
    LocationDTO findLocationById(long id);
    List<LocationDTO> findAllLocations();
    void deleteLocation(long id);
}
