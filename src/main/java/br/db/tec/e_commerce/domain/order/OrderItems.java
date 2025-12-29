package br.db.tec.e_commerce.domain.order;

import java.time.OffsetDateTime;

import br.db.tec.e_commerce.domain.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Table(
    name = "order_items",
    schema = "ECOMMERCE",
    indexes = {
        @Index(name = "idx_orders_items_orders_id", columnList = "order_id"),
        @Index(name = "idx_orders_items_product_id", columnList = "product_id")
    }
)
@Data
public class OrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name ="order_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_order_id")
    )
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name ="product_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_id")
    )
    private Product product;

    @Min(0)
    @Column(nullable = false)
    private int quantity;

    @Min(0)
    @Column(nullable = false)
    private Long unitPrice;

    @Column(columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}


