package br.devluan.easystock.utils;
import java.util.regex.Pattern;

import br.devluan.easystock.dto.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO user = (UserDTO) target;

        if (user.getName() == null || user.getName().isEmpty()) {
            errors.rejectValue("name", "name.empty", "Name cannot be empty");
        }

        if (user.getEmail() == null || !Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            errors.rejectValue("email", "email.invalid", "Invalid email format");
        }

        if (user.getPassword() == null || user.getPassword().length() < 8) {
            errors.rejectValue("password", "password.tooShort", "Password must have at least 8 characters");
        }
    }
}
