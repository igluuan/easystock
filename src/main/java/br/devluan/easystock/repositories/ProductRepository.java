package br.devluan.easystock.repositories;

import br.devluan.easystock.entities.Category;
import br.devluan.easystock.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByCategory(Category category, PageRequest of);
}
