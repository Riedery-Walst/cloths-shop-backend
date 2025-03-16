package ru.andreev.clothsshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andreev.clothsshop.dto.AddressDTO;
import ru.andreev.clothsshop.dto.RegisterDTO;
import ru.andreev.clothsshop.dto.UserDTO;
import ru.andreev.clothsshop.model.Role;
import ru.andreev.clothsshop.model.User;
import ru.andreev.clothsshop.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private RegisterDTO registerDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Вася");
        user.setLastName("Иванов");
        user.setPhone("123456789");
        user.setRole(Role.USER);

        registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password");
        registerDTO.setFirstName("Вася");
        registerDTO.setLastName("Иванов");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Вася");
        userDTO.setLastName("Иванов");
        userDTO.setPhone("123456789");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry("Country");
        addressDTO.setCity("City");
        addressDTO.setStreet("Street");
        addressDTO.setHouse("1");
        addressDTO.setApartment("10");
        addressDTO.setPostalCode("12345");
        userDTO.setAddress(addressDTO);
    }

    @Test
    void testRegisterUser() {
        when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(registerDTO);

        assertNotNull(registeredUser);
        assertEquals(registerDTO.getEmail(), registeredUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetUserProfile() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO fetchedUserDTO = userService.getUserProfile("test@example.com");

        assertNotNull(fetchedUserDTO);
        assertEquals(user.getEmail(), fetchedUserDTO.getEmail());
    }

    @Test
    void testUpdateUserProfile() {
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(1L);
        updatedUserDTO.setEmail("updated@example.com");
        updatedUserDTO.setFirstName("UpdatedName");
        updatedUserDTO.setLastName("UpdatedLastName");
        updatedUserDTO.setPhone("987654321");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO updatedUser = userService.updateUserProfile("test@example.com", updatedUserDTO);

        assertNotNull(updatedUser);
        assertEquals(updatedUserDTO.getEmail(), updatedUser.getEmail());
        assertEquals(updatedUserDTO.getFirstName(), updatedUser.getFirstName());
        assertEquals(updatedUserDTO.getLastName(), updatedUser.getLastName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUserProfile() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        userService.deleteUserProfile("test@example.com");

        verify(userRepository, times(1)).delete(user);
    }

}
