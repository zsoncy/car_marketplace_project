package hu.inf.unideb.DTOs;

import hu.inf.unideb.model.Car;
import hu.inf.unideb.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BasicUserDto {
    private Long userId;
    private String username;
    private String password;
    private List<Car> cars;
    private Role role;
}
