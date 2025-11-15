package hu.inf.unideb.service;

import hu.inf.unideb.DTOs.BasicCarDto;
import hu.inf.unideb.converter.BasicCarDtoConverter;
import hu.inf.unideb.repository.CarRepo;
import hu.inf.unideb.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CarService {
    
    private CarRepo carRepo;
    private BasicCarDtoConverter basicCarDtoConverter;
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
    public BasicCarDto getCarById(String vin) {
        return carRepo
                .findById(vin)
                .map(basicCarDtoConverter::convertCarToBasicCarDto)
                .orElseThrow(() -> new RuntimeException
                        ("Car not found with the identification number: " + vin));
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
    public BasicCarDto updateCar(String vin, BasicCarDto updatedCar){

        var existingCar = carRepo.findById(vin).orElseThrow(
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

    public void deleteCar(String vin) {
        if (!carRepo.existsById(vin)) {
            throw new RuntimeException("Car not found with the identification number: " + vin);
        }
        carRepo.deleteById(vin);
    }


}
