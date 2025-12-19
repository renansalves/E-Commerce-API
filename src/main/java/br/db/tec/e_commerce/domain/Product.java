package br.db.tec.e_commerce.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(
  name = "product",
  schema = "ecommerce",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_product_sku", columnNames = {"sku"})
  },
  indexes = {
    @Index(name = "idx_product_active", columnList = "active"),
    @Index(name = "idx_product_sku", columnList = "sku")
}
)
@Data
public class Product {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @NotBlank
 @Column(nullable = false, unique = true)
 private String sku;

 @NotBlank
 @Column(nullable = false)
 String name;

 @NotNull
 @Min(0)
 @Column(nullable = false )
 Long priceCents;

 @NotBlank
 @Column(nullable = false)
 String currency = "BRL";

 @NotNull
 @Column(nullable = false)
 private Boolean active = true;

 @Column(columnDefinition = "TIMESTAMPTZ")
 private OffsetDateTime createdAt;

 @Column(columnDefinition = "TIMESTAMPTZ")
 private OffsetDateTime updatedAt;

}
