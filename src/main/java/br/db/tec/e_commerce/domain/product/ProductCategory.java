package br.db.tec.e_commerce.domain.product;

import br.db.tec.e_commerce.domain.category.Category;
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

@Entity
@Table(
  name = "product_category",
  schema = "ecommerce"
)
@Data
public class ProductCategory {

 
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(
    name ="product_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_product_id")

  )
  private Product productId;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(
    name ="category_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_category_id")
  )
  private Category categoryId;
  
}

