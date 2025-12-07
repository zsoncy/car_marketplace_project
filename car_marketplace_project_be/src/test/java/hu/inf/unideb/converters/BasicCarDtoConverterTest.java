package hu.inf.unideb.converters;

import hu.inf.unideb.DTOs.BasicCarDto;
import hu.inf.unideb.converter.BasicCarDtoConverter;
import hu.inf.unideb.model.Car;
import hu.inf.unideb.model.Fuel;
import hu.inf.unideb.model.Transmission;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BasicCarDtoConverterTest {

    private final BasicCarDtoConverter converter =
            Mappers.getMapper(BasicCarDtoConverter.class);

    @Test
    void convertCarToBasicCarDto_validCar_mappedCorrectly() {
        // Arrange
        Car car = new Car();
        car.setVin("WDB1234567890TEST");
        car.setMake("Mercedes-Benz");
        car.setModel("C-Class");
        car.setYear(2020);
        car.setFuel(Fuel.PETROL);
        car.setTransmission(Transmission.AUTOMATIC);
        car.setEngine_size(1991);
        car.setImages_src(Arrays.asList(
                "http://example.com/car1.jpg",
                "http://example.com/car2.jpg"));
        car.setPrice(12500000); // HUF or any unit used in your domain
        car.setDescription("Sport package, low mileage");

        // Act
        BasicCarDto result = converter.convertCarToBasicCarDto(car);

        // Assert
        assertNotNull(result);
        assertEquals("WDB1234567890TEST", result.getVin());
        assertEquals("Mercedes-Benz", result.getMake());
        assertEquals("C-Class", result.getModel());
        assertEquals(2020, result.getYear());
        assertEquals(Fuel.PETROL, result.getFuel());
        assertEquals(Transmission.AUTOMATIC, result.getTransmission());
        assertEquals(1991, result.getEngine_size());
        assertNotNull(result.getImages_src());
        assertEquals(2, result.getImages_src().size());
        assertEquals("http://example.com/car1.jpg", result.getImages_src().get(0));
        assertEquals("http://example.com/car2.jpg", result.getImages_src().get(1));
        assertEquals(12500000, result.getPrice());
        assertEquals("Sport package, low mileage", result.getDescription());
        // user field is carried on DTO but we don't populate/assert it here
    }

    @Test
    void convertBasicCarDtoToCar_validDto_mappedCorrectly() {
        // Arrange
        BasicCarDto dto = new BasicCarDto();
        dto.setVin("JHMFA16586S000000");
        dto.setMake("Honda");
        dto.setModel("Civic");
        dto.setYear(2018);
        dto.setFuel(Fuel.DIESEL);
        dto.setTransmission(Transmission.MANUAL);
        dto.setEngine_size(1597);
        dto.setImages_src(Arrays.asList(
                "http://example.com/civic1.png",
                "http://example.com/civic2.png"));
        dto.setPrice(3800000);
        dto.setDescription("Well maintained, single owner");

        // Act
        Car result = converter.convertBasicCarDtoToCar(dto);

        // Assert
        assertNotNull(result);
        assertEquals("JHMFA16586S000000", result.getVin());
        assertEquals("Honda", result.getMake());
        assertEquals("Civic", result.getModel());
        assertEquals(2018, result.getYear());
        assertEquals(Fuel.DIESEL, result.getFuel());
        assertEquals(Transmission.MANUAL, result.getTransmission());
        assertEquals(1597, result.getEngine_size());
        assertNotNull(result.getImages_src());
        assertEquals(2, result.getImages_src().size());
        assertEquals("http://example.com/civic1.png", result.getImages_src().get(0));
        assertEquals("http://example.com/civic2.png", result.getImages_src().get(1));
        assertEquals(3800000, result.getPrice());
        assertEquals("Well maintained, single owner", result.getDescription());

        // The mapper ignores the 'user' field when converting DTO -> entity.
        // We mirror your Recipe test and do not assert 'user' here.
        // (See @Mapping(target = "user", ignore = true) in BasicCarDtoConverter.)
    }
}
