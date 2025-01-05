package br.devluan.easystock.dto.UserDTO;

import br.devluan.easystock.entities.Role;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        Role role
) {}

