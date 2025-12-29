package br.db.tec.e_commerce.domain.order;

import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.db.tec.e_commerce.domain.user.Users;
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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(
  name = "orders",
  schema = "ECOMMERCE"
)
@Data
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; 
  
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
    name = "user_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_orders_user_id")
  )
  private Users user;

  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(
    name = "status",
    nullable = false,
    columnDefinition = ("ecomerce.order_status_enum")
  )
  private OrderStatus orderStatus = OrderStatus.PENDING;

  @NotNull
  @Min(0)
  @Column(nullable = false)
  private Long totalCents;

  @Column(columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime createdAt;

  @Column(columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime updatedAt;
}
