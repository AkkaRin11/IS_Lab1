package org.example.ic_lab1.service;

import lombok.RequiredArgsConstructor;
import org.example.ic_lab1.domain.Person;
import org.example.ic_lab1.dto.PersonDTO;
import org.example.ic_lab1.enm.Color;
import org.example.ic_lab1.enm.Country;
import org.example.ic_lab1.mapper.CoordinatesMapper;
import org.example.ic_lab1.mapper.LocationMapper;
import org.example.ic_lab1.mapper.PersonMapper;
import org.example.ic_lab1.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public List<PersonDTO> findPersons(String filter, String sortProperty, boolean sortAscending, int offset, int limit) {
        List<PersonDTO> all = findAllPersons();

        Stream<PersonDTO> stream = all.stream();

        // Фильтрация (регистронезависимая подстрока)
        if (filter != null && !filter.trim().isEmpty()) {
            String f = filter.trim().toLowerCase();
            stream = stream.filter(p -> matchesFilter(p, f));
        }

        // Сортировка
        stream = stream.sorted((p1, p2) -> compareByProperty(p1, p2, sortProperty, sortAscending));

        // Пагинация
        return stream
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }


    private boolean matchesFilter(PersonDTO p, String filter) {
        return (p.getName() != null && p.getName().toLowerCase().contains(filter)) ||
            (p.getEyeColor() != null && p.getEyeColor().name().toLowerCase().contains(filter)) ||
            (p.getHairColor() != null && p.getHairColor().name().toLowerCase().contains(filter)) ||
            (p.getNationality() != null && p.getNationality().name().toLowerCase().contains(filter)) ||
            (p.getLocationName() != null && p.getLocationName().toLowerCase().contains(filter)) ||
            (p.getId() != null && p.getId().toString().contains(filter)) ||
            (String.valueOf(p.getCoordinatesX()).contains(filter)) ||
            (p.getCoordinatesY() != null && p.getCoordinatesY().toString().contains(filter)) ||
            (String.valueOf(p.getHeight()).contains(filter)) ||
            (p.getLocationX() != null && p.getLocationX().toString().contains(filter)) ||
            (p.getLocationY() != null && p.getLocationY().toString().contains(filter));
    }

    private int compareByProperty(PersonDTO p1, PersonDTO p2, String property, boolean ascending) {
        if (property == null || property.isEmpty()) {
            property = "id";
        }

        int result;
        switch (property) {
            case "id":
                result = Long.compare(p1.getId(), p2.getId());
                break;
            case "name":
                result = compareNullable(p1.getName(), p2.getName());
                break;
            case "coordinatesX":
                result = Double.compare(p1.getCoordinatesX(), p2.getCoordinatesX());
                break;
            case "coordinatesY":
                result = Float.compare(p1.getCoordinatesY(), p2.getCoordinatesY());
                break;
            case "height":
                result = Float.compare(p1.getHeight(), p2.getHeight());
                break;
            case "eyeColor":
                result = compareNullable(p1.getEyeColor(), p2.getEyeColor());
                break;
            case "hairColor":
                result = compareNullable(p1.getHairColor(), p2.getHairColor());
                break;
            case "nationality":
                result = compareNullable(p1.getNationality(), p2.getNationality());
                break;
            case "locationName":
                result = compareNullable(p1.getLocationName(), p2.getLocationName());
                break;
            case "locationX":
                result = compareNullable(p1.getLocationX(), p2.getLocationX());
                break;
            case "locationY":
                result = compareNullable(p1.getLocationY(), p2.getLocationY());
                break;
            default:
                result = 0;
        }

        return ascending ? result : -result;
    }

    private <T extends Comparable<T>> int compareNullable(T a, T b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }

    @Override
    public long countPersons(String filter) {
        Stream<PersonDTO> stream = findAllPersons().stream();

        if (filter != null && !filter.trim().isEmpty()) {
            String f = filter.trim().toLowerCase();
            stream = stream.filter(p -> matchesFilter(p, f));
        }

        return stream.count();
    }

    @Override
    @Transactional
    public void deleteAllByHeight(Float height) {
        if (height == null) return;
        List<Person> persons = personRepository.findByHeight(height);
        personRepository.deleteAll(persons);
    }

    @Override
    public Float sumHeight() {
        return personRepository.findAll().stream()
                .map(Person::getHeight)
                .reduce(0f, Float::sum);
    }

    @Override
    public Map<Country, Long> groupByNationality() {
        return personRepository.findAll().stream()
                .collect(Collectors.groupingBy(Person::getNationality, Collectors.counting()));
    }

    @Override
    public Float getHairColorShare(Color hairColor) {
        if (hairColor == null) return 0f;
        long total = personRepository.count();
        if (total == 0) return 0f;
        long withColor = personRepository.findByHairColor(hairColor).size();
        return (float) withColor / total * 100;
    }

    @Override
    public Integer countByHairColorAndLocation(Color hairColor, String locationName) {
        if (hairColor == null || locationName == null || locationName.trim().isEmpty()) {
            return 0;
        }
        return personRepository.findByHairColorAndLocationName(hairColor, locationName.trim()).size();
    }
}
