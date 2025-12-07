package hu.inf.unideb.converters;


import hu.inf.unideb.DTOs.BasicUserDto;
import hu.inf.unideb.converter.BasicUserDtoConverter;
import hu.inf.unideb.model.Role;
import hu.inf.unideb.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class BasicUserDtoConverterTest {

    private final BasicUserDtoConverter converter =
            Mappers.getMapper(BasicUserDtoConverter.class);

    @Test
    void convertUserToBasicUserDto_validUser_mappedCorrectly() {

        //Arrange
        User user = new User();
        user.setUserId(10L);
        user.setUsername("test_user");
        user.setPassword("secret");
        user.setRole(Role.USER);

        //Act
        BasicUserDto result = converter.convertUserToBasicUserDto(user);

        //Assert
        assertNotNull(result);
        assertEquals("test_user", result.getUsername());
        assertEquals("secret", result.getPassword());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void convertBasicUserDtoToUser_validDto_mappedCorrectly() {

        //Arrange
        BasicUserDto dto = new BasicUserDto();
        dto.setUsername("another_user");
        dto.setPassword("another_secret");
        dto.setRole(Role.ADMIN);

        //Act
        User result = converter.convertBasicUserDtoToUser(dto);

        //Assert
        assertNotNull(result);
        assertEquals("another_user", result.getUsername());
        assertEquals("another_secret", result.getPassword());
        assertEquals(Role.ADMIN, result.getRole());
        // recipes field is ignored in the mapper
        assertNull(result.getCars());
    }
}
