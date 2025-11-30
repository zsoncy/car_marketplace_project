package hu.inf.unideb.service;


import hu.inf.unideb.DTOs.LoginRequestDto;
import hu.inf.unideb.DTOs.LoginResponseDto;
import hu.inf.unideb.DTOs.RegisterRequestDto;
import hu.inf.unideb.DTOs.RegisterResponseDto;
import hu.inf.unideb.model.Role;
import hu.inf.unideb.model.User;
import hu.inf.unideb.repository.UserRepo;
import hu.inf.unideb.security.PasswordEncrypter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordEncrypter passwordEncrypter;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthService authService;


    // REGISTER TESTS

    @Test
    void register_correctUser_registerUser() {

        //Arrange
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("new_user");
        request.setPassword("securePass");

        when(userRepo.existsByUsername("new_user")).thenReturn(false);
        when(passwordEncrypter.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("securePass")).thenReturn("encodedPass");
        when(jwtService.generateToken("new_user")).thenReturn("mockedToken");

        //Act
        RegisterResponseDto response = authService.register(request);

        //Assert
        assertEquals("new_user", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals("mockedToken", response.getAccessToken());

        verify(userRepo, times(1)).save(any(User.class));
    }


    @Test
    void register_usernameTaken_throwException() {

        //Arrange
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("existing_user");
        request.setPassword("pass");

        when(userRepo.existsByUsername("existing_user")).thenReturn(true);

        //Act + Arrange
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.register(request)
        );

        assertEquals("Username already taken!", exception.getMessage());
        verify(userRepo, never()).save(any());
    }

    // LOGIN TESTS

    @Test
    void login_correctData_logIn() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("valid_user");
        request.setPassword("correctPass");

        User user = new User();
        user.setUsername("valid_user");
        user.setPassword("encodedPass");
        user.setRole(Role.USER);

        when(userRepo.findByUsername("valid_user")).thenReturn(user);
        when(passwordEncrypter.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.matches("correctPass", "encodedPass")).thenReturn(true);
        when(jwtService.generateToken("valid_user")).thenReturn("loginToken");


        LoginResponseDto response = authService.login(request);

        assertEquals("valid_user", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals("loginToken", response.getAccessToken());
    }


    @Test
    void login_noUserFound_throwException() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("unknown_user");
        request.setPassword("pass");

        when(userRepo.findByUsername("unknown_user")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(request)
        );

        assertEquals("User not found!", exception.getMessage());
    }


    @Test
    void login_wrongPassword_throwException() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("valid_user");
        request.setPassword("wrongPass");

        User user = new User();
        user.setUsername("valid_user");
        user.setPassword("encodedPass");
        user.setRole(Role.USER);


        when(userRepo.findByUsername("valid_user")).thenReturn(user);
        when(passwordEncrypter.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(request)
        );

        assertEquals("Invalid password!", exception.getMessage());
    }


}
