package org.example.ic_lab1.mapper;

import org.example.ic_lab1.domain.Coordinates;
import org.example.ic_lab1.domain.Location;
import org.example.ic_lab1.dto.CoordinatesDTO;
import org.example.ic_lab1.dto.LocationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CoordinatesMapper {
    CoordinatesDTO toDto(Coordinates coordinates);
    Coordinates toEntity(CoordinatesDTO coordinatesDTO);
}
