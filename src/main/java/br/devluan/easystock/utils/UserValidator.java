package br.devluan.easystock.utils;
import br.devluan.easystock.dto.UserDTO.UpdateUserDTO;
import br.devluan.easystock.dto.UserDTO.UserCreationDTO;
import br.devluan.easystock.exceptions.EmailAlreadyExistsException;
import br.devluan.easystock.exceptions.UserErrorExceptions;
import br.devluan.easystock.repositories.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUserCreation(UserCreationDTO createUserDto) {
        validateName(createUserDto.name());
        validateEmailForCreation(createUserDto.email());
        validatePasswordFormat(createUserDto.password());
    }

    private void validateEmailForCreation(String email) {
        if (email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }

        if (!isValidEmailFormat(email)) {
            throw new ValidationException("Invalid email format");
        }
    }

    public void validateUserUpdate(UUID userId, UpdateUserDTO updateUserDto) {
        validateUserExists(userId);

        Optional.ofNullable(updateUserDto.name())
                .ifPresent(this::validateName);

        Optional.ofNullable(updateUserDto.email())
                .ifPresent(email -> validateEmail(email, userId));

        Optional.ofNullable(updateUserDto.password())
                .ifPresent(this::validatePasswordFormat); // Renomeado para deixar claro que só valida formato
    }

    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserErrorExceptions("User not found with id: " + userId);
        }
    }

    private void validateName(String name) {
        if (name.trim().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }
        if (name.length() < 3 || name.length() > 100) {
            throw new ValidationException("Name must be between 3 and 100 characters");
        }
    }

    private void validateEmail(String email, UUID currentUserId) {
        if (email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }

        if (!isValidEmailFormat(email)) {
            throw new ValidationException("Invalid email format");
        }

        userRepository.findByEmail(email)
                .filter(user -> !user.getUserId().equals(currentUserId))
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("Email already in use: " + email);
                });
    }

    // Renomeado e simplificado para validar apenas o formato
    private void validatePasswordFormat(String password) {
        if (password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }

        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }

        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new ValidationException("Password must contain at least one letter and one number");
        }
    }

    private boolean isValidEmailFormat(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
