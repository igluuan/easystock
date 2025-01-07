package br.devluan.easystock.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String name;

    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "min_quantity")
    private Integer minQuantity;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
