package hu.inf.unideb.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    private JWTService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // minden teszt előtt új instance, saját secret key-jel
        jwtService = new JWTService();
    }

    // generateToken + getUsernameFromToken

    @Test
    void generateToken_validUsername_canReadBackUsername() {
        // Arrange
        String username = "test_user";

        // Act
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.getUsernameFromToken(token);

        // Assert
        assertNotNull(token);
        assertEquals(username, extractedUsername);
    }

    // extractTokenFromRequest

    @Test
    void extractTokenFromRequest_validHeader_returnsToken() {
        // Arrange
        String token = jwtService.generateToken("someone");
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        // Act
        String extracted = jwtService.extractTokenFromRequest(request);

        // Assert
        assertEquals(token, extracted);
    }

    @Test
    void extractTokenFromRequest_missingHeader_throwsException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jwtService.extractTokenFromRequest(request));

        assertEquals("The token was not found", ex.getMessage());
    }

    @Test
    void extractTokenFromRequest_headerWithoutBearer_throwsException() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("SomethingElse token");

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jwtService.extractTokenFromRequest(request));

        assertEquals("The token was not found", ex.getMessage());
    }

    // addToBlackList

    @Test
    void addToBlackList_validRequest_tokenAddedToList() {
        // Arrange
        String token = jwtService.generateToken("blacklistedUser");
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        // Act
        jwtService.addToBlackList(request);

        // Assert
        assertTrue(jwtService.getBlackList().contains(token));
    }

    // validateToken

    @Test
    void validateToken_correctUserAndNotExpired_returnsTrue() {
        // Arrange
        String username = "valid_user";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        // Act
        boolean result = jwtService.validateToken(token, userDetails);

        // Assert
        assertTrue(result);
    }

    @Test
    void validateToken_wrongUser_returnsFalse() {
        // Arrange
        String token = jwtService.generateToken("original_user");
        when(userDetails.getUsername()).thenReturn("other_user");

        // Act
        boolean result = jwtService.validateToken(token, userDetails);

        // Assert
        assertFalse(result);
    }
}
