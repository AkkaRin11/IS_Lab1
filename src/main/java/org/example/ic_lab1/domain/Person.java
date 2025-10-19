package org.example.ic_lab1.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.example.ic_lab1.enm.Color;
import org.example.ic_lab1.enm.Country;

import java.util.Date;

@Entity
@Table(name = "person")
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @Column(name = "name", nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id", nullable = false)
    private Coordinates coordinates; //Поле не может быть null

    @Column(name = "creationDate", nullable = false)
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Enumerated(EnumType.STRING)
    @Column(name = "eyeColor", nullable = false)
    private Color eyeColor; //Поле может быть null

    @Enumerated(EnumType.STRING)
    @Column(name = "hairColor", nullable = false)
    private Color hairColor; //Поле может быть null

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private Location location; //Поле может быть null

    @Column(name = "height")
    private float height; //Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality", nullable = false)
    private Country nationality; //Поле не может быть null

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.creationDate = now;
    }
}
