package com.bookstore.v1.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bookstore.v1.data.*;
import com.bookstore.v1.dto.UserDTO;
import com.bookstore.v1.exception.DuplicateObjectException;
import com.bookstore.v1.exception.EmptyFieldException;
import com.bookstore.v1.exception.EntityNotFoundException;
import com.bookstore.v1.exception.InvalidDoubleRange;
import com.bookstore.v1.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("1", "Raluki123", "raluca.ioana@example.com", "0745678922");
        testUser.setUserName("Raluki123");
    }

    @Test
    void testAddUser() throws EmptyFieldException {
        // Arrange
        UserDTO userDTO = new UserDTO("1","Raluki123", "raluca.ioana@example.com", "0745678922");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.addUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Raluki123", result.getUserName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser() throws EmptyFieldException {
        // Arrange
        UserDTO userDTO = new UserDTO("1","Updated userName", "Updated email", "Updated phoneNumber");

        userDTO.setId(testUser.getId());
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.updateUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated userName", result.getUserName());
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUserById() throws EntityNotFoundException {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        userService.deleteUserById(testUser.getId());

        // Assert
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testGetUsers() {
        // Arrange
        List<User> users = Collections.singletonList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTO> result = userService.getUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(testUser.getUserName(), result.get(0).getUserName());
        verify(userRepository, times(1)).findAll();
    }
}