package br.devluan.easystock.dto;

public record LoginResponse(
        String accessToken,
        Long expiresIn
) {
}
