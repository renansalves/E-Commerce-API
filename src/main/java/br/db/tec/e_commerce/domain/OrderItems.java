package br.db.tec.e_commerce.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;


@Entity
@Table(
  name = "orderItems",
  schema = "ecomerce",
  indexes = {
    @Index(name = "idx_orders_items_orders_id", columnList = "order_id")
  }
)
@Data
public class OrderItems {
  /*
  )CREATE TABLE IF NOT EXISTS ECOMERCE.order_items (
  id              BIGSERIAL PRIMARY KEY,
  order_id        BIGINT      NOT NULL REFERENCES ECOMERCE.orders(id)  ON DELETE CASCADE,
  product_id      BIGINT      NOT NULL REFERENCES ECOMERCE.product(id) ON DELETE RESTRICT,
  quantity        INTEGER     NOT NULL CHECK (quantity > 0),
  unit_price      BIGINT      NOT NULL CHECK (unit_price >= 0),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
;
   */
   
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name ="orders_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_orders_id")

      )
  private Orders orders; // refernecia ao orders(id)

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(
      name ="product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_product_id")

      )
  private Product product; // referencia ao product(id)
                           //
  @Min(0)
  @Column(nullable = false)
  private int quantity;

  @Min(0)
  @Column(nullable = false)
  private BigDecimal unitPrice;

  @Column(nullable = false, columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime createdAt;

  
}
