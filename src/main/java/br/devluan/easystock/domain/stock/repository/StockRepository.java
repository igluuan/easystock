package br.devluan.easystock.domain.stock.repository;

import br.devluan.easystock.domain.stock.entity.StockMovements;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockMovements,Long> {
}
