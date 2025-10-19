package org.example.ic_lab1.service;

import org.example.ic_lab1.dto.PersonDTO;

import java.util.List;

public interface PersonService {
    PersonDTO createPerson(PersonDTO person);
    PersonDTO updatePerson(PersonDTO person);
    PersonDTO findPersonById(long id);
    List<PersonDTO> findAllPersons();
    void deletePerson(long id);
    List<PersonDTO> findPersonsPage(int offset, int limit);
}
