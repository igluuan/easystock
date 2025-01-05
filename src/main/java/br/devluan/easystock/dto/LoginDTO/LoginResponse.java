package br.devluan.easystock.dto.LoginDTO;

public record LoginResponse(
        String accessToken,
        Long expiresIn
) {
}
