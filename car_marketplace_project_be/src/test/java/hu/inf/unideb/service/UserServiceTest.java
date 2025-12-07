package hu.inf.unideb.service;

import hu.inf.unideb.converter.BasicUserDtoConverter;
import hu.inf.unideb.DTOs.BasicUserDto;
import hu.inf.unideb.model.Role;
import hu.inf.unideb.model.User;
import hu.inf.unideb.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private BasicUserDtoConverter basicUserDtoConverter;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;


    // SAVE USER TESTS

    @Test
    void saveUser_correctUser_saved() {

        //Arrange
        BasicUserDto userToSave = new BasicUserDto();
        userToSave.setUsername("test0101test");
        userToSave.setPassword("123456789");
        userToSave.setRole(Role.USER);

        User converted = new User();
        converted.setUsername("test0101test");
        converted.setPassword("123456789");
        converted.setRole(Role.USER);

        when(basicUserDtoConverter.convertBasicUserDtoToUser(userToSave)).thenReturn(converted);


        //Act
        BasicUserDto savedUser = userService.saveUser(userToSave);

        //Assert
        assertNotNull(savedUser);
        assertEquals("test0101test", savedUser.getUsername());
        assertEquals("123456789", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());

        verify(basicUserDtoConverter, times(1)).convertBasicUserDtoToUser(userToSave);
        verify(userRepo, times(1)).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("test0101test", capturedUser.getUsername());
        assertEquals("123456789", capturedUser.getPassword());
        assertEquals(Role.USER, capturedUser.getRole());

    }


    @Test
    void saveUser_invalidUser_throwException() {

        //Arrange
        BasicUserDto invalidUser = new BasicUserDto();
        invalidUser.setUsername(null);

        when(basicUserDtoConverter.convertBasicUserDtoToUser(invalidUser)).thenThrow(new IllegalArgumentException("Invalid user data"));

        //Act + Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(invalidUser);
        });

        assertEquals("Invalid user data", exception.getMessage());

        verify(userRepo, never()).save(any());
    }


    // GET USER BY ID TESTS

    @Test
    void getUserById_correctId_returnUser() {

        //Arrange
        Long userId = 8L;

        User userInRepo = new User();
        userInRepo.setUserId(userId);
        userInRepo.setUsername("alpha_beta");
        userInRepo.setPassword("omega268");

        BasicUserDto convertedDto = new BasicUserDto();
        convertedDto.setUsername("alpha_beta");
        convertedDto.setPassword("omega268");

        when(userRepo.findById(userId)).thenReturn(Optional.of(userInRepo));
        when(basicUserDtoConverter.convertUserToBasicUserDto(userInRepo)).thenReturn(convertedDto);

        //Act
        BasicUserDto result = userService.getUserById(userId);

        //Assert
        assertNotNull(result);
        assertEquals("alpha_beta", result.getUsername());
        assertEquals("omega268", result.getPassword());

        verify(userRepo, times(1)).findById(userId);
        verify(basicUserDtoConverter, times(1)).convertUserToBasicUserDto(userInRepo);
    }

    @Test
    void getUserById_invalidId_throwException() {

        //Arrange
        Long invalidId = 2973L;

        when(userRepo.findById(invalidId)).thenReturn(Optional.empty());

        //Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(invalidId);
        });

        assertEquals("User not found with id: " + invalidId, exception.getMessage());

        verify(basicUserDtoConverter, never()).convertUserToBasicUserDto(any());
    }

    // GET ALL USERS TESTS

    @Test
    void getAllUsers_haveUsers_returnUsers() {
        // Arrange
        User user1 = new User();
        user1.setUsername("test0102");
        user1.setPassword("morty56");
        user1.setRole(Role.USER);

        User user2 = new User();
        user2.setUsername("test0103");
        user2.setPassword("flo41");
        user2.setRole(Role.USER);

        User user3 = new User();
        user3.setUsername("test0104");
        user3.setPassword("77res1");
        user3.setRole(Role.USER);

        List<User> usersFromRepo = List.of(user1, user2, user3);
        when(userRepo.findAll()).thenReturn(usersFromRepo);

        BasicUserDto dto1 = new BasicUserDto();
        dto1.setUsername("test0102");
        dto1.setPassword("morty56");
        dto1.setRole(Role.USER);

        BasicUserDto dto2 = new BasicUserDto();
        dto2.setUsername("test0103");
        dto2.setPassword("flo41");
        dto2.setRole(Role.USER);

        BasicUserDto dto3 = new BasicUserDto();
        dto3.setUsername("test0104");
        dto3.setPassword("77res1");
        dto3.setRole(Role.USER);

        when(basicUserDtoConverter.convertUserToBasicUserDto(user1)).thenReturn(dto1);
        when(basicUserDtoConverter.convertUserToBasicUserDto(user2)).thenReturn(dto2);
        when(basicUserDtoConverter.convertUserToBasicUserDto(user3)).thenReturn(dto3);

        // Act
        List<BasicUserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("test0102", result.get(0).getUsername());
        assertEquals("morty56", result.get(0).getPassword());

        assertEquals("test0103", result.get(1).getUsername());
        assertEquals("flo41", result.get(1).getPassword());

        assertEquals("test0104", result.get(2).getUsername());
        assertEquals("77res1", result.get(2).getPassword());

        verify(userRepo, times(1)).findAll();
        verify(basicUserDtoConverter, times(3)).convertUserToBasicUserDto(any(User.class));
    }

    @Test
    void getAllUsers_noUsers_returnEmptyList() {

        // Arrange
        when(userRepo.findAll()).thenReturn(List.of());

        // Act
        List<BasicUserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty result list");

        verify(userRepo, times(1)).findAll();
        verify(basicUserDtoConverter, never()).convertUserToBasicUserDto(any());
    }

    // UPDATE USER TESTS

    @Test
    void updateUser_correctUser_updatedUser() {
        Long userId = 8L;

        BasicUserDto userToUpdate = new BasicUserDto();
        userToUpdate.setUsername("gesztenye");
        userToUpdate.setPassword("ezust12");
        userToUpdate.setRole(Role.USER);

        BasicUserDto updatedUser = new BasicUserDto();
        updatedUser.setUsername("mogyoro");
        updatedUser.setPassword("arany77");
        updatedUser.setRole(Role.USER);

        when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));
        when(basicUserDtoConverter.convertUserToBasicUserDto(any(User.class))).thenReturn(userToUpdate);

        User convertedUser = new User();
        when(basicUserDtoConverter.convertBasicUserDtoToUser(any(BasicUserDto.class))).thenReturn(convertedUser);

        //Act
        BasicUserDto result = userService.updateUser(userId, updatedUser);

        //Assert
        assertEquals("mogyoro", result.getUsername());
        assertEquals("arany77", result.getPassword());
        verify(userRepo, times(1)).findById(userId);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_noUserToUpdate_throwsException() {
        // Arrange
        Long userId = 9921L;
        BasicUserDto updatedUser = new BasicUserDto();
        updatedUser.setUsername("abc123");
        updatedUser.setPassword("abc123");

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.updateUser(userId, updatedUser)
        );

        assertEquals("User not found with id: 9921", exception.getMessage());
        verify(userRepo, never()).save(any());
    }

    // DELETE USER TESTS

    @Test
    void deleteUser_haveUserToDelete_userDeleted() {

        // Arrange
        Long userId = 1L;

        User userToDelete = new User();
        userToDelete.setUserId(userId);
        userToDelete.setUsername("testUsername");
        userToDelete.setPassword("testPassword");
        userToDelete.setRole(Role.USER);
        when(userRepo.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepo, times(1)).existsById(userId);
        verify(userRepo, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_noUserToDelete_throwsException() {

        // Arrange
        Long userId = 5L;
        when(userRepo.existsById(userId)).thenReturn(false);

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.deleteUser(userId)
        );

        assertEquals("Cannot delete — user not found with id: 5", exception.getMessage());
        verify(userRepo, times(1)).existsById(userId);
        verify(userRepo, never()).deleteById(any());
    }

}
