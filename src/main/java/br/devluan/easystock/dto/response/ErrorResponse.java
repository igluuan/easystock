package br.devluan.easystock.dto.response;

import java.util.List;

public record ErrorResponse(
        String title,
        String message,
        List<String> errors
) {}
