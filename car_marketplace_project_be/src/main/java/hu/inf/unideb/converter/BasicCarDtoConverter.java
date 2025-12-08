package hu.inf.unideb.converter;

import hu.inf.unideb.DTOs.BasicCarDto;
import hu.inf.unideb.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BasicCarDtoConverter {

    @Mapping(target = "username", ignore = true)
    BasicCarDto convertCarToBasicCarDto(Car car);

    @Mapping(target = "user", ignore = true)
    Car convertBasicCarDtoToCar(BasicCarDto basicCarDto);

}
