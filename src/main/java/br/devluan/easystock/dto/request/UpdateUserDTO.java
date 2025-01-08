package br.devluan.easystock.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        @Size(min = 3, max = 50, message = "The name must be between 3 and 50 characters")
        String name,

        @Email(message = "Invalid email format")
        String email,

        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {}
