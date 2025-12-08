
package hu.inf.unideb.controller;

import hu.inf.unideb.DTOs.BasicCarDto;
import hu.inf.unideb.model.Fuel;
import hu.inf.unideb.service.CarService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;


    @Test
    void createCar_validRequest_returnsOkResponse() {
        // Arrange
        BasicCarDto requestDto = new BasicCarDto();
        requestDto.setMake("Honda");
        requestDto.setModel("Civic");
        requestDto.setFuel(Fuel.PETROL);

        BasicCarDto responseDto = new BasicCarDto();
        responseDto.setMake("Honda");
        responseDto.setModel("Civic");
        responseDto.setFuel(Fuel.PETROL);

        HttpServletRequest request = new MockHttpServletRequest();

        when(carService.createCar(requestDto, request)).thenReturn(responseDto);

        // Act
        ResponseEntity<BasicCarDto> response = carController.createCar(requestDto, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Honda", response.getBody().getMake());
        assertEquals("Civic", response.getBody().getModel());
        assertEquals(Fuel.PETROL, response.getBody().getFuel());
        verify(carService, times(1)).createCar(requestDto, request);
    }

    @Test
    void getCarById_existingId_returnsCar() {
        // Arrange
        Long id = 5L;
        BasicCarDto dto = new BasicCarDto();
        dto.setMake("BMW");
        dto.setModel("3 Series");
        dto.setFuel(Fuel.DIESEL);
        when(carService.getCarById(id)).thenReturn(dto);

        // Act
        ResponseEntity<BasicCarDto> response = carController.getCarById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BMW", response.getBody().getMake());
        assertEquals("3 Series", response.getBody().getModel());
        assertEquals(Fuel.DIESEL, response.getBody().getFuel());
        verify(carService, times(1)).getCarById(id);
    }

    @Test
    void getAllCars_existingCars_returnsList() {
        // Arrange
        BasicCarDto dto1 = new BasicCarDto();
        dto1.setMake("Audi");
        dto1.setModel("A4");

        BasicCarDto dto2 = new BasicCarDto();
        dto2.setMake("Volkswagen");
        dto2.setModel("Golf");

        List<BasicCarDto> cars = List.of(dto1, dto2);
        when(carService.getAllCars()).thenReturn(cars);

        // Act
        ResponseEntity<List<BasicCarDto>> response = carController.getAllCars();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Audi", response.getBody().get(0).getMake());
        assertEquals("Volkswagen", response.getBody().get(1).getMake());
        verify(carService, times(1)).getAllCars();
    }

    /*
    @Test
    void updateCar_validRequest_returnsUpdatedCar() {
        // Arrange
        Long id = 10L;
        BasicCarDto updateDto = new BasicCarDto();
        updateDto.setMake("Toyota");
        updateDto.setModel("Corolla");
        updateDto.setTransmission(Transmission.AUTOMATIC);

        BasicCarDto updatedResult = new BasicCarDto();
        updatedResult.setMake("Toyota");
        updatedResult.setModel("Corolla");
        updatedResult.setTransmission(Transmission.AUTOMATIC);

        when(carService.updateCar(id, updateDto)).thenReturn(updatedResult);

        // Act
        ResponseEntity<BasicCarDto> response = carController.updateCar(id, updateDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Toyota", response.getBody().getMake());
        assertEquals("Corolla", response.getBody().getModel());
        assertEquals(Transmission.AUTOMATIC, response.getBody().getTransmission());
        verify(carService, times(1)).updateCar(id, updateDto);
    }

    @Test
    void deleteCar_existingId_returnsOkMessage() {
        // Arrange
        Long id = 7L;

        // Act
        ResponseEntity<String> response = carController.deleteCar(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: CarController currently returns "Car with id:" + id + "has been deleted" (no space before 'has').
        assertEquals("Car with id:" + id + "has been deleted", response.getBody());
        verify(carService, times(1)).deleteCar(id);
    }

     */
}
