package org.example.ic_lab1.service;

import org.example.ic_lab1.dto.CoordinatesDTO;

import java.util.List;

public interface CoordinatesService {
    CoordinatesDTO createCoordinates(CoordinatesDTO coordinates);
    CoordinatesDTO updateCoordinates(CoordinatesDTO coordinatesDTO);
    CoordinatesDTO findCoordinatesById(long id);
    List<CoordinatesDTO> findAllCoordinates();
    void deleteCoordinates(long id);
}
