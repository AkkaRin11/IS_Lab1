package org.example.ic_lab1.contoller;

import lombok.RequiredArgsConstructor;
import org.example.ic_lab1.dto.CoordinatesDTO;
import org.example.ic_lab1.service.CoordinatesService;
import org.example.ic_lab1.service.CoordinatesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordinates")
@RequiredArgsConstructor
public class CoordinatesController {
    private final CoordinatesService coordinatesService;

    @PostMapping
    public ResponseEntity<CoordinatesDTO> createCoordinates(@RequestBody CoordinatesDTO coordinatesDTO) {
        CoordinatesDTO coordinates = coordinatesService.createCoordinates(coordinatesDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(coordinates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordinatesDTO> getCoordinatesById(@PathVariable long id) {
        CoordinatesDTO coordinatesDTO = coordinatesService.findCoordinatesById(id);
        return ResponseEntity.status(HttpStatus.OK).body(coordinatesDTO);
    }

    @GetMapping()
    public ResponseEntity<List<CoordinatesDTO>> getAllCoordinatess() {
        List<CoordinatesDTO> listCoordinatesDTO = coordinatesService.findAllCoordinates();
        return ResponseEntity.status(HttpStatus.OK).body(listCoordinatesDTO);
    }

    @PutMapping()
    public ResponseEntity<CoordinatesDTO> updateCoordinates(
            @RequestBody CoordinatesDTO coordinatesDTO) {

        if (coordinatesDTO.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        CoordinatesDTO updated = coordinatesService.updateCoordinates(coordinatesDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoordinates(@PathVariable long id) {
        coordinatesService.deleteCoordinates(id);
        return ResponseEntity.noContent().build();
    }
}