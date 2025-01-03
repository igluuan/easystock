package br.devluan.easystock.dto;

public record LoginRequest(
        String email,
        String password
) {
}
