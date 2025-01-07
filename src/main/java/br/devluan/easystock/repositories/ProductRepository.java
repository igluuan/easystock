package br.devluan.easystock.repositories;

import br.devluan.easystock.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
