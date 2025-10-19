package org.example.ic_lab1.contoller;

import lombok.RequiredArgsConstructor;
import org.example.ic_lab1.domain.Location;
import org.example.ic_lab1.dto.LocationDTO;
import org.example.ic_lab1.dto.LocationDTO;
import org.example.ic_lab1.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {
        LocationDTO location = locationService.createLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(location);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable long id) {
        LocationDTO locationDTO = locationService.findLocationById(id);
        return ResponseEntity.status(HttpStatus.OK).body(locationDTO);
    }

    @GetMapping()
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<LocationDTO> locationsDTO = locationService.findAllLocations();
        return ResponseEntity.status(HttpStatus.OK).body(locationsDTO);
    }

    @PutMapping()
    public ResponseEntity<LocationDTO> updateLocation(
            @RequestBody LocationDTO locationDTO) {

        if (locationDTO.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        LocationDTO updated = locationService.updateLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
