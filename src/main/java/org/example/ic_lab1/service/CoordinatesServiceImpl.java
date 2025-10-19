package org.example.ic_lab1.service;

import lombok.RequiredArgsConstructor;
import org.example.ic_lab1.domain.Coordinates;
import org.example.ic_lab1.dto.CoordinatesDTO;
import org.example.ic_lab1.mapper.CoordinatesMapper;
import org.example.ic_lab1.repository.CoordinatesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoordinatesServiceImpl implements CoordinatesService {
    private final CoordinatesRepository coordinatesRepository;
    private final CoordinatesMapper coordinatesMapper;

    @Override
    @Transactional
    public CoordinatesDTO createCoordinates(CoordinatesDTO coordinatesDTO) {
        Coordinates coordinates = coordinatesMapper.toEntity(coordinatesDTO);
        return coordinatesMapper.toDto(coordinatesRepository.save(coordinates));
    }

    @Override
    @Transactional
    public CoordinatesDTO updateCoordinates(CoordinatesDTO coordinatesDTO) {
        Coordinates existingCoordinates = coordinatesRepository.findById(coordinatesDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Coordinates not found"));

        existingCoordinates.setX(coordinatesDTO.getX());
        existingCoordinates.setY(coordinatesDTO.getY());

        Coordinates updatedCoordinates = coordinatesRepository.save(existingCoordinates);
        return coordinatesMapper.toDto(updatedCoordinates);
    }

    @Override
    public CoordinatesDTO findCoordinatesById(long id) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Coordinates with id " + id + " not found"));
        return coordinatesMapper.toDto(coordinates);
    }

    @Override
    public List<CoordinatesDTO> findAllCoordinates() {
        return coordinatesRepository.findAll()
                .stream()
                .map(coordinatesMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCoordinates(long id) {
        coordinatesRepository.deleteById(id);
    }
}
