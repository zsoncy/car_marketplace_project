package hu.inf.unideb.service;

import hu.inf.unideb.converter.BasicCarDtoConverter;
import hu.inf.unideb.DTOs.BasicCarDto;
import hu.inf.unideb.model.Car;
import hu.inf.unideb.model.Role;
import hu.inf.unideb.model.User;
import hu.inf.unideb.repository.CarRepo;
import hu.inf.unideb.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CarService {
    
    private CarRepo carRepo;
    private BasicCarDtoConverter basicCarDtoConverter;
    private JWTService jwtService;
    private UserRepo userRepo;

    //CREATE
    public BasicCarDto createCar(BasicCarDto basicCarDto, String username) {
        var owner = userRepo.findByUsername(username);
        System.out.println(owner);
        var carToSave = basicCarDtoConverter.convertBasicCarDtoToCar(basicCarDto);
        owner.addCar(carToSave);
        carRepo.save(carToSave);
        return basicCarDto;
    }

    //READ - Get by ID
    public BasicCarDto getCarById(Long id) {
        return carRepo
                .findById(id)
                .map(basicCarDtoConverter::convertCarToBasicCarDto)
                .orElseThrow(() -> new RuntimeException
                        ("Car not found with id: " + id));
    }

    //READ - Get all cars
    public List<BasicCarDto> getAllCars() {
        return carRepo
                .findAll()
                .stream()
                .map(basicCarDtoConverter::convertCarToBasicCarDto)
                .toList();
    }

    //UPDATE
    public BasicCarDto updateCar(Long id, BasicCarDto updatedCar){

        var existingCar = carRepo.findById(id).orElseThrow(
                ()-> new RuntimeException("Car not found")
        );

        existingCar.setMake(updatedCar.getMake());
        existingCar.setModel(updatedCar.getModel());
        existingCar.setYear(updatedCar.getYear());
        existingCar.setFuel(updatedCar.getFuel());
        existingCar.setTransmission(updatedCar.getTransmission());
        existingCar.setEngine_size(updatedCar.getEngine_size());
        existingCar.setImages_src(updatedCar.getImages_src());
        existingCar.setPrice(updatedCar.getPrice());
        existingCar.setDescription(updatedCar.getDescription());
        existingCar.setUser(updatedCar.getUser());

        carRepo.save(existingCar);

        return updatedCar;
    }

    public void deleteCar(Long id) {
        if (!carRepo.existsById(id)) {
            throw new RuntimeException("Car not found with id: " + id);
        }
        carRepo.deleteById(id);
    }

    public List<BasicCarDto> getUserCar(HttpServletRequest request) {
        var user = getUserFromRequest(request);
        return carRepo.findAll().stream()
                .map(car -> {
                    var response = basicCarDtoConverter.convertCarToBasicCarDto(car);
                    response.setUsername(car.getUser().getUsername());
                    return response;
                })
                .filter(car -> car.getUsername().equals(user.getUsername()))
                .toList();
    }

    private User getUserFromRequest(HttpServletRequest request) {
        var tokenFromRequest = jwtService.extractTokenFromRequest(request);
        var username = jwtService.getUsernameFromToken(tokenFromRequest);
        return userRepo.findByUsername(username);
    }

    private boolean checkOwnerShip(String username, Car car) {
        var user = userRepo.findByUsername(username);
        return (car.getUser().getUsername().equals(username)
                || user.getRole().equals(Role.ADMIN));
    }


}
