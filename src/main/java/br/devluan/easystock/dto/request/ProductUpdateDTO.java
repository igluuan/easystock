package br.devluan.easystock.dto.request;

import br.devluan.easystock.dto.base.ProductBaseDTO;

public record ProductUpdateDTO(
        String name,
        Double price,
        Integer stockQuantity,
        Integer minQuantity
) implements ProductBaseDTO {}
