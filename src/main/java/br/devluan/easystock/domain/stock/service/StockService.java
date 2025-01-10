package br.devluan.easystock.domain.stock.service;

import br.devluan.easystock.domain.stock.entity.StockMovements;
import br.devluan.easystock.dto.response.StockResponseDTO;
import br.devluan.easystock.domain.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final StockRepository stockRepository;

    public StockResponseDTO createLog(StockMovements stockMovements){

    }
}
