package hu.inf.unideb.converter;

import hu.inf.unideb.DTOs.BasicUserDto;
import hu.inf.unideb.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BasicUserDtoConverter {

    BasicUserDto convertUserToBasicUserDto(User user);
    @Mapping(target = "cars", ignore = true)
    User convertBasicUserDtoToUser(BasicUserDto basicUserDto);
}
