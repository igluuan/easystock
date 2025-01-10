package br.devluan.easystock.services;

import br.devluan.easystock.domain.user.service.UserService;
import br.devluan.easystock.dto.request.LoginRequest;
import br.devluan.easystock.dto.response.LoginResponse;
import br.devluan.easystock.dto.response.PageResponseDTO;
import br.devluan.easystock.dto.request.UpdateUserDTO;
import br.devluan.easystock.dto.request.UserCreationDTO;
import br.devluan.easystock.dto.response.UserResponseDTO;
import br.devluan.easystock.domain.user.entity.Role;
import br.devluan.easystock.domain.user.entity.User;
import br.devluan.easystock.application.exceptions.UserExceptions.UserErrorExceptions;
import br.devluan.easystock.domain.user.mapper.UserMapper;
import br.devluan.easystock.domain.user.repository.RoleRepository;
import br.devluan.easystock.domain.user.repository.UserRepository;
import br.devluan.easystock.common.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldCreateSuccessfully() {
        // Arrange
        UserCreationDTO creationDTO = new UserCreationDTO("John Doe", "john@example.com", "password123");
        User userEntity = new User();
        userEntity.setName("John Doe");
        userEntity.setEmail("john@example.com");
        Role employeeRole = new Role(Role.Values.EMPLOYEE.name());

        when(roleRepository.findByName(Role.Values.EMPLOYEE.name())).thenReturn(employeeRole);
        when(userMapper.toEntity(creationDTO)).thenReturn(userEntity);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toResponseDTO(userEntity)).thenReturn(new UserResponseDTO(
                UUID.randomUUID(), "John Doe", "john@example.com", "EMPLOYEE"
        ));

        // Act
        UserResponseDTO result = userService.createUser(creationDTO);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(userValidator).validateUserCreation(creationDTO);
        verify(passwordEncoder).encode(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void authenticate_ShouldReturnTokenWhenCredentialsAreValid() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password123");
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ADMIN"));
        user.setRoles(roles);

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtEncoder.encode(any())).thenReturn(new Jwt(
                "mockedTokenValue12345",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "HS256"),
                Map.of(
                        "iss", "backend",
                        "sub", "123e4567-e89b-12d3-a456-426614174000",
                        "scope", "ADMIN"
                )
        ));

        // Act
        LoginResponse response = userService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertEquals(300L, response.expiresIn());
    }


    @Test
    void authenticate_ShouldThrowBadCredentialsException_WhenUserNotFound() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "password");
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userService.authenticate(loginRequest));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUserId(userId);
        user.setName("John Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(new UserResponseDTO(
                userId, "John Doe", "john@example.com", "EMPLOYEE"
        ));

        // Act
        UserResponseDTO result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals("John Doe", result.name());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserErrorExceptions.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_ShouldUpdateSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpdateUserDTO updateDTO = new UpdateUserDTO("Updated Name", "updated@example.com", "newPassword");
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setName("Original Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(new UserResponseDTO(
                userId, "Updated Name", "updated@example.com", "EMPLOYEE"
        ));

        // Act
        UserResponseDTO result = userService.updateUser(userId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.name());
        assertEquals("updated@example.com", result.email());
        verify(userValidator).validateUserUpdate(userId, updateDTO);
    }

    @Test
    void toggleStatus_ShouldToggleUserStatus() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUserId(userId);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        boolean result = userService.toggleStatus(userId);

        // Assert
        assertFalse(result);
        verify(userRepository).save(user);
    }

    @Test
    void getUsers_ShouldReturnPagedResponse() {
        // Arrange
        User user = new User();
        user.setName("Test User");
        List<User> users = List.of(user);
        Page<User> pagedUsers = new PageImpl<>(users);

        UserResponseDTO userDTO = new UserResponseDTO(
                UUID.randomUUID(), "Test User", "test@example.com", "EMPLOYEE"
        );

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(pagedUsers);
        when(userMapper.toResponseDTO(user)).thenReturn(userDTO);

        // Act
        PageResponseDTO<UserResponseDTO> result = userService.getUsers(0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(1, result.content().size());
        assertEquals("Test User", result.content().get(0).name());
    }
}
