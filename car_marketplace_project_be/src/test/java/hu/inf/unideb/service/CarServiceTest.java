package hu.inf.unideb.service;

import hu.inf.unideb.DTOs.BasicCarDto;
import hu.inf.unideb.converter.BasicCarDtoConverter;
import hu.inf.unideb.model.*;
import hu.inf.unideb.repository.CarRepo;
import hu.inf.unideb.repository.UserRepo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepo carRepo;

    @Mock
    private BasicCarDtoConverter basicCarDtoConverter;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private CarService carService;

    @Captor
    private ArgumentCaptor<Car> carCaptor;

    // CREATE CAR TESTS
    @Test
    void createCar_correctCar_created() {

        // Arrange
        String username = "random_user_01";
        User owner = new User();
        owner.setUsername(username);
        owner.setPassword("123456789");
        owner.setRole(Role.USER);
        owner.setCars(new ArrayList<>());
        userRepo.save(owner);

        BasicCarDto carToCreate = new BasicCarDto();
        carToCreate.setVin("VIN-123456789");
        carToCreate.setMake("Toyota");
        carToCreate.setModel("Corolla");
        carToCreate.setYear(2020);
        carToCreate.setFuel(Fuel.PETROL);
        carToCreate.setTransmission(Transmission.MANUAL);
        carToCreate.setEngine_size(1598);
        carToCreate.setImages_src(List.of("http://example/pic1.jpg"));
        carToCreate.setPrice(4500);
        carToCreate.setDescription("Clean car, single owner.");
        carToCreate.setUser(owner);

        Car converted = new Car();
        converted.setVin("VIN-123456789");
        converted.setMake("Toyota");
        converted.setModel("Corolla");
        converted.setYear(2020);
        converted.setFuel(Fuel.PETROL);
        converted.setTransmission(Transmission.MANUAL);
        converted.setEngine_size(1598);
        converted.setImages_src(List.of("http://example/pic1.jpg"));
        converted.setPrice(4500);
        converted.setDescription("Clean car, single owner.");
        converted.setUser(owner);

        when(userRepo.findByUsername(username)).thenReturn(owner);
        when(basicCarDtoConverter.convertBasicCarDtoToCar(carToCreate)).thenReturn(converted);

        // Act
        BasicCarDto createdCar = carService.createCar(carToCreate, username);

        // Assert
        assertNotNull(createdCar);
        assertEquals("VIN-123456789", createdCar.getVin());
        assertEquals("Toyota", createdCar.getMake());
        assertEquals("Corolla", createdCar.getModel());
        assertEquals(2020, createdCar.getYear());
        assertEquals(Fuel.PETROL, createdCar.getFuel());
        assertEquals(Transmission.MANUAL, createdCar.getTransmission());
        assertEquals(1598, createdCar.getEngine_size());
        assertEquals(List.of("http://example/pic1.jpg"), createdCar.getImages_src());
        assertEquals(4500, createdCar.getPrice());
        assertEquals("Clean car, single owner.", createdCar.getDescription());
        assertEquals(owner, createdCar.getUser());

        verify(userRepo, times(1)).findByUsername(username);
        verify(basicCarDtoConverter, times(1)).convertBasicCarDtoToCar(carToCreate);
        verify(carRepo, times(1)).save(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertEquals("VIN-123456789", capturedCar.getVin());
        assertEquals("Toyota", capturedCar.getMake());
        assertEquals("Corolla", capturedCar.getModel());
        assertEquals(2020, capturedCar.getYear());
        assertEquals(Fuel.PETROL, capturedCar.getFuel());
        assertEquals(Transmission.MANUAL, capturedCar.getTransmission());
        assertEquals(1598, capturedCar.getEngine_size());
        assertEquals(List.of("http://example/pic1.jpg"), capturedCar.getImages_src());
        assertEquals(4500, capturedCar.getPrice());
        assertEquals("Clean car, single owner.", capturedCar.getDescription());
        assertEquals(owner, capturedCar.getUser());
    }

    @Test
    void createCar_invalidUser_throwsException() {

        // Arrange
        BasicCarDto carToCreate = new BasicCarDto();
        carToCreate.setVin("VIN-000000001");
        carToCreate.setMake("Honda");
        carToCreate.setModel("Civic");
        carToCreate.setYear(2018);
        carToCreate.setFuel(Fuel.DIESEL);
        carToCreate.setTransmission(Transmission.AUTOMATIC);
        carToCreate.setEngine_size(1498);
        carToCreate.setImages_src(List.of("http://example/civic.jpg"));
        carToCreate.setPrice(5200);
        carToCreate.setDescription("Nice diesel civic.");

        when(userRepo.findByUsername("invalid_username")).thenReturn(null);

        // Act + Assert
        assertThrows(NullPointerException.class, () ->
                carService.createCar(carToCreate, "invalid_username")
        );

        verify(userRepo, times(1)).findByUsername("invalid_username");
        verify(carRepo, never()).save(any());
    }

    // GET CAR BY ID TESTS
    @Test
    void getCarById_correctId_returnCar() {

        // Arrange
        Long carId = 13L;
        Car carInRepo = new Car();
        carInRepo.setId(carId);
        carInRepo.setVin("VIN-ABC123");
        carInRepo.setMake("BMW");
        carInRepo.setModel("320d");
        carInRepo.setYear(2017);
        carInRepo.setFuel(Fuel.DIESEL);
        carInRepo.setTransmission(Transmission.AUTOMATIC);
        carInRepo.setEngine_size(1995);
        carInRepo.setImages_src(List.of("http://example/bmw.jpg"));
        carInRepo.setPrice(9800);
        carInRepo.setDescription("Well-maintained.");

        BasicCarDto convertedDto = new BasicCarDto();
        convertedDto.setVin("VIN-ABC123");
        convertedDto.setMake("BMW");
        convertedDto.setModel("320d");
        convertedDto.setYear(2017);
        convertedDto.setFuel(Fuel.DIESEL);
        convertedDto.setTransmission(Transmission.AUTOMATIC);
        convertedDto.setEngine_size(1995);
        convertedDto.setImages_src(List.of("http://example/bmw.jpg"));
        convertedDto.setPrice(9800);
        convertedDto.setDescription("Well-maintained.");

        when(carRepo.findById(carId)).thenReturn(Optional.of(carInRepo));
        when(basicCarDtoConverter.convertCarToBasicCarDto(carInRepo)).thenReturn(convertedDto);

        // Act
        BasicCarDto result = carService.getCarById(carId);

        // Assert
        assertNotNull(result);
        assertEquals("VIN-ABC123", result.getVin());
        assertEquals("BMW", result.getMake());
        assertEquals("320d", result.getModel());
        assertEquals(2017, result.getYear());
        assertEquals(Fuel.DIESEL, result.getFuel());
        assertEquals(Transmission.AUTOMATIC, result.getTransmission());
        assertEquals(1995, result.getEngine_size());
        assertEquals(List.of("http://example/bmw.jpg"), result.getImages_src());
        assertEquals(9800, result.getPrice());
        assertEquals("Well-maintained.", result.getDescription());

        verify(carRepo, times(1)).findById(carId);
        verify(basicCarDtoConverter, times(1)).convertCarToBasicCarDto(carInRepo);
    }

    @Test
    void getCarById_invalidId_throwsException() {
        // Arrange
        Long invalidId = 999L;
        when(carRepo.findById(invalidId)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                carService.getCarById(invalidId)
        );
        assertEquals("Car not found with id: 999", exception.getMessage());

        verify(carRepo, times(1)).findById(invalidId);
        verify(basicCarDtoConverter, never()).convertCarToBasicCarDto(any());
    }

    // GET ALL CARS
    @Test
    void getAllCars_haveCars_returnCars() {

        // Arrange
        Car car1 = new Car();
        car1.setVin("VIN-111");
        car1.setMake("Audi");
        car1.setModel("A4");
        car1.setYear(2019);
        car1.setFuel(Fuel.PETROL);
        car1.setTransmission(Transmission.MANUAL);
        car1.setEngine_size(1984);
        car1.setImages_src(List.of("http://example/a4.jpg"));
        car1.setPrice(11500);
        car1.setDescription("Sport package.");

        Car car2 = new Car();
        car2.setVin("VIN-222");
        car2.setMake("Tesla");
        car2.setModel("Model 3");
        car2.setYear(2021);
        car2.setFuel(Fuel.ELECTRIC);
        car2.setTransmission(Transmission.AUTOMATIC);
        car2.setEngine_size(0);
        car2.setImages_src(List.of("http://example/model3.jpg"));
        car2.setPrice(30000);
        car2.setDescription("Long Range.");

        when(carRepo.findAll()).thenReturn(List.of(car1, car2));

        BasicCarDto dto1 = new BasicCarDto();
        dto1.setVin("VIN-111");
        dto1.setMake("Audi");
        dto1.setModel("A4");
        dto1.setYear(2019);
        dto1.setFuel(Fuel.PETROL);
        dto1.setTransmission(Transmission.MANUAL);
        dto1.setEngine_size(1984);
        dto1.setImages_src(List.of("http://example/a4.jpg"));
        dto1.setPrice(11500);
        dto1.setDescription("Sport package.");

        BasicCarDto dto2 = new BasicCarDto();
        dto2.setVin("VIN-222");
        dto2.setMake("Tesla");
        dto2.setModel("Model 3");
        dto2.setYear(2021);
        dto2.setFuel(Fuel.ELECTRIC);
        dto2.setTransmission(Transmission.AUTOMATIC);
        dto2.setEngine_size(0);
        dto2.setImages_src(List.of("http://example/model3.jpg"));
        dto2.setPrice(30000);
        dto2.setDescription("Long Range.");

        when(basicCarDtoConverter.convertCarToBasicCarDto(car1)).thenReturn(dto1);
        when(basicCarDtoConverter.convertCarToBasicCarDto(car2)).thenReturn(dto2);

        // Act
        List<BasicCarDto> result = carService.getAllCars();

        // Assert
        assertEquals(2, result.size());
        verify(carRepo, times(1)).findAll();
        verify(basicCarDtoConverter, times(2)).convertCarToBasicCarDto(any(Car.class));
    }

    @Test
    void getAllCars_noCars_returnEmptyList() {

        // Arrange
        when(carRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<BasicCarDto> result = carService.getAllCars();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty result list");
        verify(carRepo, times(1)).findAll();
        verify(basicCarDtoConverter, never()).convertCarToBasicCarDto(any());
    }

    // UPDATE CAR TESTS
    @Test
    void updateCar_correctCar_updatedCar() {

        // Arrange
        Long carId = 42L;

        Car existingCar = new Car();
        existingCar.setId(carId);
        existingCar.setMake("Old Make");
        existingCar.setModel("Old Model");

        User owner = new User();
        owner.setUsername("owner_42");

        BasicCarDto updatedDto = new BasicCarDto();
        updatedDto.setVin("VIN-424242");
        updatedDto.setMake("New Make");
        updatedDto.setModel("New Model");
        updatedDto.setYear(2022);
        updatedDto.setFuel(Fuel.HYBRID);
        updatedDto.setTransmission(Transmission.AUTOMATIC);
        updatedDto.setEngine_size(1999);
        updatedDto.setImages_src(List.of("http://example/new_pic.jpg"));
        updatedDto.setPrice(20000);
        updatedDto.setDescription("Updated Description");
        updatedDto.setUser(owner);

        when(carRepo.findById(carId)).thenReturn(Optional.of(existingCar));

        // Act
        BasicCarDto result = carService.updateCar(carId, updatedDto);

        // Assert
        assertEquals("New Make", result.getMake());
        assertEquals("New Model", result.getModel());
        assertEquals("Updated Description", result.getDescription());
        verify(carRepo, times(1)).findById(carId);
        verify(carRepo, times(1)).save(existingCar);
    }

    @Test
    void updateCar_noCarToUpdate_throwsException() {

        // Arrange
        Long invalidId = 404L;
        BasicCarDto updatedCar = new BasicCarDto();
        updatedCar.setMake("Updated Make");
        updatedCar.setModel("Updated Model");
        updatedCar.setDescription("Updated Description");

        when(carRepo.findById(invalidId)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                carService.updateCar(invalidId, updatedCar)
        );

        assertEquals("Car not found", exception.getMessage());
        verify(carRepo, times(1)).findById(invalidId);
        verify(carRepo, never()).save(any());
    }

    // DELETE CAR TESTS
    @Test
    void deleteCar_haveCarToDelete_carDeleted() {

        // Arrange
        Long carId = 10L;
        when(carRepo.existsById(carId)).thenReturn(true);

        // Act
        carService.deleteCar(carId);

        // Assert
        verify(carRepo, times(1)).existsById(carId);
        verify(carRepo, times(1)).deleteById(carId);
    }

    @Test
    void deleteCar_noCarToDelete_throwsException() {

        // Arrange
        Long carId = 99L;
        when(carRepo.existsById(carId)).thenReturn(false);

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                carService.deleteCar(carId)
        );
        assertEquals("Car not found with id: 99", exception.getMessage());

        verify(carRepo, times(1)).existsById(carId);
        verify(carRepo, never()).deleteById(any());
    }
}
