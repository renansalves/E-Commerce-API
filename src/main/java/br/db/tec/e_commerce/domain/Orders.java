// **Implementação**: `Order(id, user_id, status enum, total, timestamps)` / `OrderItem(order_id, product_id, quantity, unitPrice)`

package br.db.tec.e_commerce.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.security.core.userdetails.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
  name = "orders",
  schema = "ecomerce",
  indexes = {
    @Index(name = "idx_orders_user_id_status", columnList = "user_id, status"),
}
)
@Data
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; 
  
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
    name = "users_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_orders_user_id")
  )
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(
    nullable = false,
    columnDefinition = ("ecomerce.order_status_enum")
  )
  private OrderStatus orderStatus = OrderStatus.PENDING;

  @Min(0)
  @Column(nullable = false)
  private BigDecimal totalCents;

  @Column(nullable = false, columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime createdAt;

  @Column(nullable = false, columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime updatedAt;
}
