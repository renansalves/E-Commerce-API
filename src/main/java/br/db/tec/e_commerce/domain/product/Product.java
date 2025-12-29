package br.db.tec.e_commerce.domain.product;

import java.time.OffsetDateTime;

import br.db.tec.e_commerce.domain.category.Category;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
  name = "product",
  schema = "ECOMMERCE",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_product_sku", columnNames = {"sku"})
  },
  indexes = {
    @Index(name = "idx_product_active", columnList = "active"),
    @Index(name = "idx_product_sku", columnList = "sku")
}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
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

 @Column(nullable = false)
 private String description;

 @NotNull
 @Min(0)
 @Column(nullable = false )
 Long priceCents;

 @NotBlank
 @Column(nullable = false)
 String currency = "BRL";

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(
  name = "category_id",
  nullable = false,
  foreignKey = @ForeignKey(name = "fk_product_category")
 )
 private Category category;

 @Column(nullable = false)
 private Integer stockQuantity = 1;
 @NotNull
 @Column(nullable = false)
 private Boolean active = true;

 @Column(columnDefinition = "TIMESTAMPTZ")
 private OffsetDateTime createdAt = OffsetDateTime.now();

 @Column(columnDefinition = "TIMESTAMPTZ")
 private OffsetDateTime updatedAt = OffsetDateTime.now();

}
