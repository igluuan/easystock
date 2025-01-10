package br.devluan.easystock.domain.stock.mapper;

import br.devluan.easystock.domain.stock.entity.StockMovements;
import br.devluan.easystock.dto.response.StockResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMapper {

    StockMovements toEntity(StockResponseDTO responseDTO);

    StockResponseDTO toResponseDTO(StockMovements entity);
}
