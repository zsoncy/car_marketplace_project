package hu.inf.unideb.service;

import hu.inf.unideb.DTOs.BasicCarDto;
import hu.inf.unideb.converter.BasicCarDtoConverter;
import hu.inf.unideb.model.*;
import hu.inf.unideb.repository.CarRepo;
import hu.inf.unideb.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.security.Principal;
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

    @Mock
    private JWTService jwtService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CarService carService;

    @Captor
    private ArgumentCaptor<Car> carCaptor;



    // CREATE CAR TESTS
    @Test
    void createCar_correctCar_created() {
        // Arrange
        String username = "random_user_01";

        // --- create a spy of the service so we can stub getUserFromRequest(...) ---
        CarService serviceSpy = spy(carService);

        // mock request (we don't care what's inside, because we stub getUserFromRequest)
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Owner that the service should use
        User owner = new User();
        owner.setUsername(username);
        owner.setPassword("123456789");
        owner.setRole(Role.USER);
        owner.setCars(new ArrayList<>());
        // if your test previously relied on saving this, you can keep it; it's not required though:
        // userRepo.save(owner);

        // DTO to create
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

        // Entity produced by the converter
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

        // --- critical stubbing: bypass internal username extraction & repo lookup ---
        doReturn(owner).when(serviceSpy).getUserFromRequest(request);

        // the converter behavior
        when(basicCarDtoConverter.convertBasicCarDtoToCar(carToCreate)).thenReturn(converted);

        // Act
        BasicCarDto createdCar = serviceSpy.createCar(carToCreate, request);

        // Assert (service currently returns the same DTO)
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

        // Verify we converted & saved the entity
        verify(basicCarDtoConverter, times(1)).convertBasicCarDtoToCar(carToCreate);
        verify(carRepo, times(1)).save(carCaptor.capture());

        // Verify owner was updated and saved entity fields
        Car saved = carCaptor.getValue();
        assertEquals("VIN-123456789", saved.getVin());
        assertEquals("Toyota", saved.getMake());
        assertEquals("Corolla", saved.getModel());
        assertEquals(2020, saved.getYear());
        assertEquals(Fuel.PETROL, saved.getFuel());
        assertEquals(Transmission.MANUAL, saved.getTransmission());
        assertEquals(1598, saved.getEngine_size());
        assertEquals(List.of("http://example/pic1.jpg"), saved.getImages_src());
        assertEquals(4500, saved.getPrice());
        assertEquals("Clean car, single owner.", saved.getDescription());
        assertEquals(owner, saved.getUser());
        assertTrue(owner.getCars().contains(saved));

        // Since we stubbed getUserFromRequest to return the owner directly,
        // there is no need to involve userRepo at all:
        verifyNoInteractions(userRepo);
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

        // Minimal change: use a request instead of username
        // This request has no user info, so getUserFromRequest(request) should return null
        HttpServletRequest request = new MockHttpServletRequest();

        // Act + Assert
        assertThrows(NullPointerException.class, () ->
                carService.createCar(carToCreate, request)
        );

        // Verify the car is not saved
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

    // GET ALL CARS TESTS
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

        User owner = new User();
        owner.setUsername("owner_42");
        owner.setRole(Role.USER);

        Car existingCar = new Car();
        existingCar.setId(carId);
        existingCar.setMake("Old Make");
        existingCar.setModel("Old Model");
        existingCar.setUser(owner);

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

        // mocks for getUserFromRequest + checkOwnerShip
        when(jwtService.extractTokenFromRequest(request)).thenReturn("token-42");
        when(jwtService.getUsernameFromToken("token-42")).thenReturn("owner_42");
        when(userRepo.findByUsername("owner_42")).thenReturn(owner);

        // Act
        BasicCarDto result = carService.updateCar(carId, updatedDto, request);

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
                carService.updateCar(invalidId, updatedCar, request)
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

        User owner = new User();
        owner.setUsername("user10");
        owner.setRole(Role.USER);

        Car car = new Car();
        car.setId(carId);
        car.setUser(owner);

        when(carRepo.findById(carId)).thenReturn(Optional.of(car));

        when(jwtService.extractTokenFromRequest(request)).thenReturn("token-10");
        when(jwtService.getUsernameFromToken("token-10")).thenReturn("user10");
        when(userRepo.findByUsername("user10")).thenReturn(owner);

        // Act
        carService.deleteCar(carId, request);

        // Assert
        verify(carRepo, times(1)).findById(carId);
        verify(carRepo, times(1)).deleteById(carId);
    }

    @Test
    void deleteCar_noCarToDelete_throwsException() {
        // Arrange
        Long carId = 99L;
        when(carRepo.findById(carId)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                carService.deleteCar(carId, request)
        );
        assertEquals("Car was not found", exception.getMessage());

        verify(carRepo, times(1)).findById(carId);
        verify(carRepo, never()).deleteById(any());
    }


    // AUTH TESTS
    @Test
    void getUserCar_authenticatedUser_returnsOnlyOwnCars() {
        // Arrange
        User alice = new User();
        alice.setUsername("alice");
        alice.setRole(Role.USER);

        User bob = new User();
        bob.setUsername("bob");
        bob.setRole(Role.USER);

        Car carA1 = new Car();
        carA1.setVin("VIN-A1");
        carA1.setUser(alice);

        Car carB1 = new Car();
        carB1.setVin("VIN-B1");
        carB1.setUser(bob);

        when(carRepo.findAll()).thenReturn(List.of(carA1, carB1));

        BasicCarDto dtoA1 = new BasicCarDto();
        dtoA1.setVin("VIN-A1");

        BasicCarDto dtoB1 = new BasicCarDto();
        dtoB1.setVin("VIN-B1");

        when(basicCarDtoConverter.convertCarToBasicCarDto(carA1)).thenReturn(dtoA1);
        when(basicCarDtoConverter.convertCarToBasicCarDto(carB1)).thenReturn(dtoB1);

        when(jwtService.extractTokenFromRequest(request)).thenReturn("token-alice");
        when(jwtService.getUsernameFromToken("token-alice")).thenReturn("alice");
        when(userRepo.findByUsername("alice")).thenReturn(alice);

        // Act
        List<BasicCarDto> result = carService.getUserCar(request);

        // Assert
        assertEquals(1, result.size());
        assertEquals("VIN-A1", result.get(0).getVin());
        assertEquals("alice", result.get(0).getUsername());

        verify(carRepo, times(1)).findAll();
        verify(basicCarDtoConverter, times(2)).convertCarToBasicCarDto(any(Car.class));
    }

    @Test
    void updateCar_notOwner_throwsException() {
        // Arrange
        Long carId = 55L;

        User realOwner = new User();
        realOwner.setUsername("real_owner");
        realOwner.setRole(Role.USER);

        User attacker = new User();
        attacker.setUsername("attacker");
        attacker.setRole(Role.USER);

        Car existingCar = new Car();
        existingCar.setId(carId);
        existingCar.setUser(realOwner);

        BasicCarDto updated = new BasicCarDto();
        updated.setMake("Hacked Make");
        updated.setModel("Hacked Model");

        when(carRepo.findById(carId)).thenReturn(Optional.of(existingCar));

        when(jwtService.extractTokenFromRequest(request)).thenReturn("token-attacker");
        when(jwtService.getUsernameFromToken("token-attacker")).thenReturn("attacker");
        when(userRepo.findByUsername("attacker")).thenReturn(attacker);

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                carService.updateCar(carId, updated, request)
        );
        assertEquals("You are not the owner of the car, or don't have permission to edit the car", ex.getMessage());

        verify(carRepo, times(1)).findById(carId);
        verify(carRepo, never()).save(any());
    }

    @Test
    void updateCar_adminUser_canUpdate() {
        // Arrange
        Long carId = 77L;

        User admin = new User();
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        User owner = new User();
        owner.setUsername("owner77");
        owner.setRole(Role.USER);

        Car existingCar = new Car();
        existingCar.setId(carId);
        existingCar.setUser(owner);

        BasicCarDto updated = new BasicCarDto();
        updated.setMake("Admin Make");
        updated.setModel("Admin Model");
        updated.setDescription("Updated by admin");

        when(carRepo.findById(carId)).thenReturn(Optional.of(existingCar));

        when(jwtService.extractTokenFromRequest(request)).thenReturn("token-admin");
        when(jwtService.getUsernameFromToken("token-admin")).thenReturn("admin");
        when(userRepo.findByUsername("admin")).thenReturn(admin);

        // Act
        BasicCarDto result = carService.updateCar(carId, updated, request);

        // Assert
        assertEquals("Admin Make", result.getMake());
        assertEquals("Admin Model", result.getModel());
        assertEquals("Updated by admin", result.getDescription());
        verify(carRepo, times(1)).save(existingCar);
    }
}
