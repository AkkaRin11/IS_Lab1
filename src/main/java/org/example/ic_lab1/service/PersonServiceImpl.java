package org.example.ic_lab1.service;

import lombok.RequiredArgsConstructor;
import org.example.ic_lab1.domain.Person;
import org.example.ic_lab1.dto.PersonDTO;
import org.example.ic_lab1.mapper.CoordinatesMapper;
import org.example.ic_lab1.mapper.LocationMapper;
import org.example.ic_lab1.mapper.PersonMapper;
import org.example.ic_lab1.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final LocationMapper locationMapper;
    private final CoordinatesMapper coordinatesMapper;

    @Override
    @Transactional
    public PersonDTO createPerson(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);
        return personMapper.toDto(personRepository.save(person));
    }

    @Override
    @Transactional
    public PersonDTO updatePerson(PersonDTO personDTO) {
        Person existingPerson = personRepository.findById(personDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Person not found"));

        existingPerson.setName(personDTO.getName());
        existingPerson.setCoordinates(coordinatesMapper.toEntity(personDTO.getCoordinates()));
        existingPerson.setEyeColor(personDTO.getEyeColor());
        existingPerson.setHairColor(personDTO.getHairColor());
        existingPerson.setLocation(locationMapper.toEntity(personDTO.getLocation()));
        existingPerson.setHeight(personDTO.getHeight());
        existingPerson.setNationality(personDTO.getNationality());

        Person updatedPerson = personRepository.save(existingPerson);
        return personMapper.toDto(updatedPerson);
    }

    @Override
    public PersonDTO findPersonById(long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Person with id " + id + " not found"));
        return personMapper.toDto(person);
    }

    @Override
    public List<PersonDTO> findAllPersons() {
        return personRepository.findAll()
                .stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePerson(long id) {
        personRepository.deleteById(id);
    }

    @Override
    public List<PersonDTO> findPersonsPage(int offset, int limit) {
        List<Person> persons = personRepository.findPage(offset, limit);
        return persons.stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }
}
