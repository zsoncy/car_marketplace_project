package hu.inf.unideb.controller;

import hu.inf.unideb.DTOs.BasicCarDto;
import hu.inf.unideb.service.CarService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@AllArgsConstructor
public class CarController {
    private final CarService carService;


    @PostMapping
    public ResponseEntity<BasicCarDto> createCar(@NonNull @RequestBody BasicCarDto basicCarDto, @RequestParam String username) {
        System.out.println(basicCarDto);
        return ResponseEntity.ok(carService.createCar(basicCarDto,username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasicCarDto> getCarById(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @GetMapping
    public ResponseEntity<List<BasicCarDto>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BasicCarDto> updateCar(@NonNull @PathVariable Long id,
                                                       @NonNull @RequestBody BasicCarDto updateCar,
                                                 HttpServletRequest request) {

        return ResponseEntity.ok(carService.updateCar(id, updateCar,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(@NonNull @PathVariable Long id,HttpServletRequest request) {
        carService.deleteCar(id,request);
        return ResponseEntity.ok("Car with id:" + id + "has been deleted");
    }

    @GetMapping("/myCars")
    public ResponseEntity<List<BasicCarDto>> userCars(HttpServletRequest request) {
        return ResponseEntity.ok(carService.getUserCar(request));
    }
}
