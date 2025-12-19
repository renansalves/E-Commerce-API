package br.db.tec.e_commerce.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(
  name = "users",
  schema = "ecommerce"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false,unique = true )
  private String email;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = ("ecomerce.user_role_enum"))
  private UserRole role;

  @Column(nullable = false)
  private Boolean enabled = true;

  @Column(columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime createdAt;

}
