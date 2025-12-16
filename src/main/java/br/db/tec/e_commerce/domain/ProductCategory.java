package br.db.tec.e_commerce.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(
  name = "product_category",
  schema = "ecomerce"
)
@Data
public class ProductCategory {

 @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
 @JoinColumn(
    name ="product_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_product_id")

  )
  private Long productId;

 @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
 @JoinColumn(
    name ="category_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_category_id")

  )
  private Long categoryId;
  
}

