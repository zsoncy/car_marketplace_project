package hu.inf.unideb.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;


    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Car> cars;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void addCar(Car car){
        car.setUser(this);
        cars.add(car);
    }

    public void removeCar(Car car){
        car.setUser(null);
        cars.remove(car);
    }


}

