package br.devluan.easystock.dto.response;

import br.devluan.easystock.domain.stock.entity.StockMovements.MovementsType;

import java.time.LocalDateTime;

public record StockResponseDTO(
        Long logId,
        MovementsType movementType,
        ProductResponse product,
        UserResponse user,
        Integer quantity,
        LocalDateTime creationAt
) {
    public record ProductResponse(
            Long productId,
            String name
    ){}
    public record UserResponse(
            Long userId,
            String name
    ){}
}
