package br.db.tec.e_commerce.domain.cart;

import java.time.OffsetDateTime;

import br.db.tec.e_commerce.domain.user.Users;
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
import lombok.Data;
import lombok.Getter;

@Entity
@Table(
  name = "carts",
  schema = "ecommerce"
)
@Data
@Getter
public class Carts {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name ="user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_users_id")
      )
  private Users user;

  @Column(columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime createdAt;
  @Column(columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime updatedAt;
}
