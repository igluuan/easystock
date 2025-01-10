package br.devluan.easystock.domain.stock.entity;

import br.devluan.easystock.domain.product.entity.Product;
import br.devluan.easystock.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MovementType type;

    @Enumerated(EnumType.STRING)
    private MovementSubtype subtype;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private Integer previousQuantity;

    private Integer currentQuantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "operation_date")
    private LocalDateTime operationDate;

    private String observation;

    @Enumerated(EnumType.STRING)
    private MovementStatus status;

    public enum MovementType {
        INBOUND, OUTBOUND, ADJUSTMENT
    }

    public enum MovementSubtype {
        PURCHASE, SALE, RETURN, LOSS, COUNT_ADJUSTMENT, CORRECTION
    }

    public enum MovementStatus {
        PENDING, COMPLETED, CANCELLED
    }
}