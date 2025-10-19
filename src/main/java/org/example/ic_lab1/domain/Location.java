package org.example.ic_lab1.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "location")
@Data
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x", nullable = false)
    private Double x; //Поле не может быть null

    @Column(name = "y", nullable = false)
    private Double y; //Поле не может быть null

    @Column(name = "name", nullable = false)
    private String name; //Поле не может быть null
}