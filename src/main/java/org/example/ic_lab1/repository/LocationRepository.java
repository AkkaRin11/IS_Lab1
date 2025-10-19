package org.example.ic_lab1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.ic_lab1.domain.Location;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class LocationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Location save(Location location) {
        if (location.getId() == null) {
            entityManager.persist(location);
            entityManager.flush();
            return location;
        } else {
            return entityManager.merge(location);
        }
    }

    public Optional<Location> findById(Long id) {
        Location location = entityManager.find(Location.class, id);
        return Optional.ofNullable(location);
    }

    public void deleteById(Long id) {
        Location location = entityManager.find(Location.class, id);
        if (location != null) {
            entityManager.remove(entityManager.contains(location) ? location : entityManager.merge(location));
        }
    }

    public List<Location> findAll() {
        TypedQuery<Location> query = entityManager.createQuery(
                "SELECT l FROM Location l", Location.class
        );
        return query.getResultList();
    }
}