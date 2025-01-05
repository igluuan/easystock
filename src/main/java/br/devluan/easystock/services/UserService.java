package br.devluan.easystock.services;

import br.devluan.easystock.dto.UserDTO.UpdateUserDTO;
import br.devluan.easystock.dto.UserDTO.UserCreationDTO;
import br.devluan.easystock.dto.UserDTO.UserResponseDTO;
import br.devluan.easystock.entities.Role;
import br.devluan.easystock.entities.User;
import br.devluan.easystock.exceptions.UserErrorExceptions;
import br.devluan.easystock.mappers.UserMapper;
import br.devluan.easystock.repositories.RoleRepository;
import br.devluan.easystock.repositories.UserRepository;
import br.devluan.easystock.utils.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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

    public UserResponseDTO getUserById(UUID userId){
        logger.info("Fetching user by id {}", userId);
        User userExisting = userRepository.findById(userId)
                .orElseThrow(() -> new UserErrorExceptions("User not found"));
        return userMapper.toResponseDTO(userExisting);
    }

    public Page<UserResponseDTO>getUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        Page<UserResponseDTO> userResponseDTOs = users.map(userMapper::toResponseDTO);
        return userResponseDTOs;
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
