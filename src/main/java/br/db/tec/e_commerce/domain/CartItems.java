package br.db.tec.e_commerce.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Table(
  name = "cart_items",
  schema = "ecommerce"
)
@Data
public class CartItems {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(
      name ="cart_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_carts_id")
      )
  private Carts carts;

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
  @Column(
  name = "unit_price_snapshot",
  nullable = false
  )
  private Double unitPrice;

  @Column(columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime createdAt;

}
