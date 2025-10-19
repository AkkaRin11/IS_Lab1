package org.example.ic_lab1.mapper;

import org.example.ic_lab1.domain.Location;
import org.example.ic_lab1.domain.Person;
import org.example.ic_lab1.dto.LocationDTO;
import org.example.ic_lab1.dto.PersonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {
    LocationDTO toDto(Location location);
    Location toEntity(LocationDTO locationDTO);
}
