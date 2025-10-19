package org.example.ic_lab1.mapper;

import org.example.ic_lab1.domain.Person;
import org.example.ic_lab1.dto.PersonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper {
    PersonDTO toDto(Person person);
    Person toEntity(PersonDTO personDTO);
}
