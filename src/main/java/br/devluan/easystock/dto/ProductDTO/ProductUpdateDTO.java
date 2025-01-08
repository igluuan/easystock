package br.devluan.easystock.dto.ProductDTO;

public record ProductUpdateDTO(
        String name,
        Double price,
        Integer stockQuantity,
        Integer minQuantity
) implements ProductBaseDTO {}
