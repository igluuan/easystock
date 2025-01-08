package br.devluan.easystock.dto.response;

public record ProductResponseDTO(
        Long id,
        String name,
        Double price,
        Integer stockQuantity,
        Integer minQuantity,
        CategoryResponseDTO category
) {
    public record CategoryResponseDTO(
            Long id,
            String name
    ) {}
}


