package org.example.ic_lab1.dto;

import lombok.Data;

@Data
public class LocationDTO {
    private Long id;
    private Double x; //Поле не может быть null
    private Double y; //Поле не может быть null
    private String name; //Поле не может быть null
}
