package br.devluan.easystock.dto.LoginDTO;

public record LoginRequest(
        String email,
        String password
) {
}
