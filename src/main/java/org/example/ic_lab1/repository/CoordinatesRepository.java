package org.example.ic_lab1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.ic_lab1.domain.Coordinates;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CoordinatesRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Coordinates save(Coordinates coordinates) {
        if (coordinates.getId() == null) {
            entityManager.persist(coordinates);
            entityManager.flush();
            return coordinates;
        } else {
            return entityManager.merge(coordinates);
        }
    }

    public Optional<Coordinates> findById(Long id) {
        Coordinates c = entityManager.find(Coordinates.class, id);
        return Optional.ofNullable(c);
    }

    public void deleteById(Long id) {
        Coordinates c = entityManager.find(Coordinates.class, id);
        if (c != null) {
            entityManager.remove(entityManager.contains(c) ? c : entityManager.merge(c));
        }
    }

    public List<Coordinates> findAll() {
        TypedQuery<Coordinates> query = entityManager.createQuery(
                "SELECT c FROM Coordinates c", Coordinates.class
        );
        return query.getResultList();
    }
}