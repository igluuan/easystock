package br.devluan.easystock.services;

import br.devluan.easystock.dto.request.LoginRequest;
import br.devluan.easystock.dto.response.LoginResponse;
import br.devluan.easystock.dto.response.PageResponseDTO;
import br.devluan.easystock.dto.request.UpdateUserDTO;
import br.devluan.easystock.dto.request.UserCreationDTO;
import br.devluan.easystock.dto.response.UserResponseDTO;
import br.devluan.easystock.domain.entities.Role;
import br.devluan.easystock.domain.entities.User;
import br.devluan.easystock.domain.exceptions.UserExceptions.UserErrorExceptions;
import br.devluan.easystock.mappers.UserMapper;
import br.devluan.easystock.repositories.RoleRepository;
import br.devluan.easystock.repositories.UserRepository;
import br.devluan.easystock.services.utils.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final JwtEncoder jwtEncoder;

    public UserResponseDTO createUser(UserCreationDTO user){
        Role roleEmployee = roleRepository.findByName(Role.Values.EMPLOYEE.name());
        logger.info("Creating new user {}", user.name());
        try {
            userValidator.validateUserCreation(user);
            User userEntity = userMapper.toEntity(user);
            userEntity.setRoles(Set.of(roleEmployee));
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            User savedUser = userRepository.save(userEntity);
            logger.info("New user created {}", savedUser.getName());
            return userMapper.toResponseDTO(savedUser);
        } catch (RuntimeException e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials."));

        if (!user.isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("backend")
                .subject(user.getUserId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue, expiresIn);
    }

    public UserResponseDTO getUserById(UUID userId){
        logger.info("Fetching user by id {}", userId);
        User userExisting = userRepository.findById(userId)
                .orElseThrow(() -> new UserErrorExceptions("User not found"));
        return userMapper.toResponseDTO(userExisting);
    }

    public PageResponseDTO<UserResponseDTO> getUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        Page<UserResponseDTO> userResponseDTOs = users.map(userMapper::toResponseDTO);
        return PageResponseDTO.from(userResponseDTOs);
    }

    public UserResponseDTO updateUser(UUID userId, UpdateUserDTO updateUserDto) {
        userValidator.validateUserUpdate(userId, updateUserDto);
        logger.info("initiating account information change {}", updateUserDto.name());
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserErrorExceptions("User not found with id: " + userId));
        Optional.ofNullable(updateUserDto.name())
                .ifPresent(existingUser::setName);

        Optional.ofNullable(updateUserDto.email())
                .ifPresent(existingUser::setEmail);

        Optional.ofNullable(updateUserDto.password())
                .map(passwordEncoder::encode)
                .ifPresent(existingUser::setPassword);

        existingUser.setUpdateAt(LocalDateTime.now());

        User savedUser = userRepository.save(existingUser);
        logger.info("change made successfully {}", savedUser.getName());
        return userMapper.toResponseDTO(savedUser);
    }

    public boolean toggleStatus(UUID userId) {
        logger.info("Toggling status for user {}", userId);

        User userExisting = userRepository.findById(userId)
                .orElseThrow(() -> new UserErrorExceptions("User not found with id: " + userId));

        userExisting.setActive(!userExisting.isActive());
        userRepository.save(userExisting);

        logger.info("Status toggled successfully for user {}", userId);

        return userExisting.isActive();
    }


}
