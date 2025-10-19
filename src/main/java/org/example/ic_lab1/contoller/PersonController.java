package org.example.ic_lab1.contoller;

import lombok.RequiredArgsConstructor;
import org.example.ic_lab1.dto.PersonDTO;
import org.example.ic_lab1.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@RequestBody PersonDTO personDTO) {
        PersonDTO newPerson = personService.createPerson(personDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPerson);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable long id) {
        PersonDTO personDTO = personService.findPersonById(id);
        return ResponseEntity.status(HttpStatus.OK).body(personDTO);
    }

    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getAllPersons() {
        List<PersonDTO> personsDTO = personService.findAllPersons();
        return ResponseEntity.status(HttpStatus.OK).body(personsDTO);
    }

    @PutMapping()
    public ResponseEntity<PersonDTO> updatePerson(
            @RequestBody PersonDTO personDTO) {

        if (personDTO.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        PersonDTO updated = personService.updatePerson(personDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}
