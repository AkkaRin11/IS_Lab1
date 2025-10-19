package org.example.ic_lab1.dto;

import lombok.Data;
import org.example.ic_lab1.enm.Color;
import org.example.ic_lab1.enm.Country;

@Data
public class PersonDTO {
    private Long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private CoordinatesDTO coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Color eyeColor; //Поле может быть null
    private Color hairColor; //Поле может быть null
    private LocationDTO location; //Поле может быть null
    private float height; //Значение поля должно быть больше 0
    private Country nationality; //Поле не может быть null

    public double getCoordinatesX(){
        return coordinates.getX();
    }

    public Float getCoordinatesY(){
        return coordinates.getY();
    }

    public Double getLocationX(){
        return location.getX();
    }

    public Double getLocationY(){
        return location.getY();
    }

    public String getLocationName(){
        return location.getName();
    }

    public void setCoordinatesX(double x){
        coordinates.setX(x);
    }

    public void setCoordinatesY(float y){
        coordinates.setY(y);
    }

    public void setLocationX(double x){
        location.setX(x);
    }

    public void setLocationY(double y){
        location.setY(y);
    }

    public void setLocationName(String name){
        location.setName(name);
    }

}
