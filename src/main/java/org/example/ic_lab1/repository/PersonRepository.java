package org.example.ic_lab1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.ic_lab1.domain.Person;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class PersonRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Person save(Person person) {
        if (person.getId() == null) {
            entityManager.persist(person);
            entityManager.flush();
            return person;
        } else {
            return entityManager.merge(person);
        }
    }

    public Optional<Person> findById(Long id) {
        Person person = entityManager.find(Person.class, id);
        return Optional.ofNullable(person);
    }

    public void deleteById(Long id) {
        Person person = entityManager.find(Person.class, id);
        if (person != null) {
            entityManager.remove(entityManager.contains(person) ? person : entityManager.merge(person));
        }
    }

    public List<Person> findAll() {
        TypedQuery<Person> query = entityManager.createQuery(
                "SELECT p FROM Person p", Person.class
        );
        return query.getResultList();
    }

    public List<Person> findPage(int offset, int limit) {
        String jpql = "SELECT p FROM Person p WHERE p.name LIKE :filter";
        TypedQuery<Person> query = entityManager.createQuery(jpql, Person.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Person> findByHeight(Float height) {
        if (height == null) {
            return List.of();
        }
        TypedQuery<Person> query = entityManager.createQuery(
                "SELECT p FROM Person p WHERE p.height = :height", Person.class
        );
        query.setParameter("height", height);
        return query.getResultList();
    }

    public List<Person> findByHairColor(org.example.ic_lab1.enm.Color hairColor) {
        if (hairColor == null) {
            return List.of();
        }
        TypedQuery<Person> query = entityManager.createQuery(
                "SELECT p FROM Person p WHERE p.hairColor = :hairColor", Person.class
        );
        query.setParameter("hairColor", hairColor);
        return query.getResultList();
    }

    public List<Person> findByHairColorAndLocationName(
            org.example.ic_lab1.enm.Color hairColor,
            String locationName
    ) {
        if (hairColor == null || locationName == null) {
            return List.of();
        }
        TypedQuery<Person> query = entityManager.createQuery(
                "SELECT p FROM Person p WHERE p.hairColor = :hairColor AND p.location.name = :locationName", Person.class
        );
        query.setParameter("hairColor", hairColor);
        query.setParameter("locationName", locationName);
        return query.getResultList();
    }

    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Person p", Long.class
        );
        return query.getSingleResult();
    }

    public List<Person> findPageUnfiltered(int offset, int limit) {
        TypedQuery<Person> query = entityManager.createQuery(
                "SELECT p FROM Person p ORDER BY p.id", Person.class
        );
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public void deleteAll(Iterable<Person> persons) {
        if (persons == null) return;
        for (Person person : persons) {
            if (person != null && person.getId() != null) {
                deleteById(person.getId());
            }
        }
    }
}