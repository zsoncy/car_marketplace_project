package hu.inf.unideb.DTOs;

import jakarta.persistence.Cache;
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
    private List<Cache> cars;
}
