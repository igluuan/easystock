package br.devluan.easystock.dto.response;

import java.util.UUID;

public record UserResponseDTO(
        UUID userId,
        String name,
        String email,
        String role
) {}

