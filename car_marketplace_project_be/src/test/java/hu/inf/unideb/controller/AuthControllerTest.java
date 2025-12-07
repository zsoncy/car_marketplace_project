package hu.inf.unideb.controller;


import hu.inf.unideb.DTOs.LoginRequestDto;
import hu.inf.unideb.DTOs.LoginResponseDto;
import hu.inf.unideb.DTOs.RegisterRequestDto;
import hu.inf.unideb.DTOs.RegisterResponseDto;
import hu.inf.unideb.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;


    // REGISTER TESTS

    @Test
    void register_validRequest_returnsCreatedResponse() {

        //Arrange
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("new_user");
        request.setPassword("securePass");

        RegisterResponseDto responseDto = RegisterResponseDto.builder()
                .username("new_user")
                .role("USER")
                .accessToken("token123")
                .build();

        when(authService.register(request)).thenReturn(responseDto);

        //Act
        ResponseEntity<RegisterResponseDto> response = authController.register(request);

        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new_user", response.getBody().getUsername());
        assertEquals("USER", response.getBody().getRole());
        assertEquals("token123", response.getBody().getAccessToken());

        verify(authService, times(1)).register(request);
    }


    // LOGIN TESTS

    @Test
    void login_validRequest_returnsOkResponse() {

        //Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("valid_user");
        request.setPassword("correctPass");

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .username("valid_user")
                .role("USER")
                .accessToken("loginToken")
                .build();

        when(authService.login(request)).thenReturn(responseDto);

        //Act
        ResponseEntity<LoginResponseDto> response = authController.login(request);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("valid_user", response.getBody().getUsername());
        assertEquals("USER", response.getBody().getRole());
        assertEquals("loginToken", response.getBody().getAccessToken());

        verify(authService, times(1)).login(request);
    }


    // LOGOUT TEST

    @Test
    void logout_validRequest_returnsOkResponse() {

        //Arrange
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(authService.Logout(servletRequest)).thenReturn("Successfully logged out");

        //Act
        ResponseEntity<String> response = authController.logout(servletRequest);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully logged out", response.getBody());
        verify(authService, times(1)).Logout(servletRequest);
    }


    // EXCEPTION HANDLER TESTS

    @Test
    void handle_usernameTaken_returnsConflict() {

        //Arrange
        RuntimeException ex = new RuntimeException("Username already taken!");

        //Act
        ResponseEntity<String> response = authController.handle(ex);

        //Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already taken!", response.getBody());
    }

    @Test
    void handle_invalidOrNotFound_returnsUnauthorized() {

        //Arrange
        RuntimeException ex1 = new RuntimeException("Invalid password!");
        RuntimeException ex2 = new RuntimeException("User not found!");

        //Act
        ResponseEntity<String> response1 = authController.handle(ex1);
        ResponseEntity<String> response2 = authController.handle(ex2);

        //Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response1.getStatusCode());
        assertEquals("Invalid password!", response1.getBody());

        assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());
        assertEquals("User not found!", response2.getBody());
    }

    @Test
    void handle_otherRuntimeException_returnsBadRequest() {

        //Arrange
        RuntimeException ex = new RuntimeException("Some other error");

        //Act
        ResponseEntity<String> response = authController.handle(ex);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Some other error", response.getBody());
    }
}
