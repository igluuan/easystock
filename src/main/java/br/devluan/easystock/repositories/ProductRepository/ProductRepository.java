package br.devluan.easystock.repositories.ProductRepository;

import br.devluan.easystock.entities.Product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
