package br.devluan.easystock.dto.request;

import br.devluan.easystock.dto.base.ProductBaseDTO;

public record ProductCreationDTO(
        String name,
        Double price,
        Integer stockQuantity,
        Integer minQuantity,
        Long categoryId
) implements ProductBaseDTO {

}
