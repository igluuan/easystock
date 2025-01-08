package br.devluan.easystock.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
