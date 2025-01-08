package br.devluan.easystock.dto.ProductDTO;

public record ProductCreationDTO(
        String name,
        Double price,
        Integer stockQuantity,
        Integer minQuantity,
        Long categoryId
) {

}
