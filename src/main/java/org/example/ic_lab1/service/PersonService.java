package org.example.ic_lab1.service;

import org.example.ic_lab1.dto.PersonDTO;
import org.example.ic_lab1.enm.Color;
import org.example.ic_lab1.enm.Country;

import java.util.List;
import java.util.Map;

public interface PersonService {
    PersonDTO createPerson(PersonDTO person);
    PersonDTO updatePerson(PersonDTO person);
    PersonDTO findPersonById(long id);
    List<PersonDTO> findAllPersons();
    void deletePerson(long id);
    List<PersonDTO> findPersonsPage(int offset, int limit);
    List<PersonDTO> findPersons(String filter, String sortProperty, boolean sortAscending, int offset, int limit);
    long countPersons(String filter);
    void deleteAllByHeight(Float height);
    Float sumHeight();
    Map<Country, Long> groupByNationality();
    Float getHairColorShare(Color hairColor);
    Integer countByHairColorAndLocation(Color hairColor, String locationName);
}
