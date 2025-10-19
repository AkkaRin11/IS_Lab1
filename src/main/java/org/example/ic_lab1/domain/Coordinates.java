package org.example.ic_lab1.domain;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coordinates")
@Data
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x", nullable = false)
    private double x; //Значение поля должно быть больше -620

    @Column(name = "y", nullable = false)
    private Float y; //Максимальное значение поля: 647, Поле не может быть null
}