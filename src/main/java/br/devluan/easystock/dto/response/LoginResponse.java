package br.devluan.easystock.dto.response;

public record LoginResponse(
        String accessToken,
        Long expiresIn
) {
}
