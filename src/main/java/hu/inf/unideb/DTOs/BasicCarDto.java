package hu.inf.unideb.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.inf.unideb.model.Fuel;
import hu.inf.unideb.model.Transmission;
import hu.inf.unideb.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BasicCarDto {
    private String vin;
    private String make;
    private String model;
    private int year;
    private Fuel fuel;
    private Transmission transmission;
    private int engine_size;
    private List<String> images_src;
    private int price;
    private String description;
    @JsonIgnore
    private User user;
}
