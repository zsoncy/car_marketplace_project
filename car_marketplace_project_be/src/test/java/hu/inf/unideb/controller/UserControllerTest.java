package hu.inf.unideb.controller;


import hu.inf.unideb.DTOs.BasicUserDto;
import hu.inf.unideb.model.Role;
import hu.inf.unideb.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    @Test
    void saveUser_validRequest_returnsOkResponse() {

        //Arrange
        BasicUserDto requestDto = new BasicUserDto();
        requestDto.setUsername("new_user");
        requestDto.setPassword("pass123");
        requestDto.setRole(Role.USER);

        BasicUserDto responseDto = new BasicUserDto();
        responseDto.setUsername("new_user");
        responseDto.setPassword("pass123");
        responseDto.setRole(Role.USER);

        when(userService.saveUser(requestDto)).thenReturn(responseDto);

        //Act
        ResponseEntity<BasicUserDto> response = userController.saveUser(requestDto);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new_user", response.getBody().getUsername());
        assertEquals("pass123", response.getBody().getPassword());
        assertEquals(Role.USER, response.getBody().getRole());

        verify(userService, times(1)).saveUser(requestDto);
    }


    @Test
    void getUserById_existingId_returnsUser() {

        //Arrange
        Long id = 3L;

        BasicUserDto dto = new BasicUserDto();
        dto.setUsername("alpha");
        dto.setPassword("beta");
        dto.setRole(Role.ADMIN);

        when(userService.getUserById(id)).thenReturn(dto);

        //Act
        ResponseEntity<BasicUserDto> response = userController.getUserById(id);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("alpha", response.getBody().getUsername());
        assertEquals(Role.ADMIN, response.getBody().getRole());

        verify(userService, times(1)).getUserById(id);
    }


    @Test
    void getAllUsers_existingUsers_returnsList() {

        //Arrange
        BasicUserDto dto1 = new BasicUserDto();
        dto1.setUsername("u1");
        dto1.setPassword("p1");
        dto1.setRole(Role.USER);

        BasicUserDto dto2 = new BasicUserDto();
        dto2.setUsername("u2");
        dto2.setPassword("p2");
        dto2.setRole(Role.ADMIN);

        List<BasicUserDto> users = List.of(dto1, dto2);

        when(userService.getAllUsers()).thenReturn(users);

        //Act
        ResponseEntity<List<BasicUserDto>> response = userController.getAllUsers();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("u1", response.getBody().get(0).getUsername());
        assertEquals("u2", response.getBody().get(1).getUsername());

        verify(userService, times(1)).getAllUsers();
    }


    @Test
    void updateUser_validRequest_returnsUpdatedUser() {

        //Arrange
        Long id = 5L;

        BasicUserDto updatedUser = new BasicUserDto();
        updatedUser.setUsername("updated");
        updatedUser.setPassword("newPass");
        updatedUser.setRole(Role.USER);

        BasicUserDto resultDto = new BasicUserDto();
        resultDto.setUsername("updated");
        resultDto.setPassword("newPass");
        resultDto.setRole(Role.USER);

        when(userService.updateUser(id, updatedUser)).thenReturn(resultDto);

        //Act
        ResponseEntity<BasicUserDto> response =
                userController.updateUser(updatedUser, id);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updated", response.getBody().getUsername());
        assertEquals("newPass", response.getBody().getPassword());
        assertEquals(Role.USER, response.getBody().getRole());

        verify(userService, times(1)).updateUser(id, updatedUser);
    }


    @Test
    void deleteUser_existingId_returnsOkMessage() {

        //Arrange
        Long id = 9L;

        //Act
        ResponseEntity<String> response = userController.deleteUser(id);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User with the id of: 9has been deleted", response.getBody());
        verify(userService, times(1)).deleteUser(id);
    }
}
