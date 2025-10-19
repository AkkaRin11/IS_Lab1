package org.example.ic_lab1.dto;

import lombok.Data;

@Data
public class CoordinatesDTO {
    private Long id;
    private double x; //Значение поля должно быть больше -620
    private Float y; //Максимальное значение поля: 647, Поле не может быть null
}
