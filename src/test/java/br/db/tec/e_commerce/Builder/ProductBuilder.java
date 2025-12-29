package br.db.tec.e_commerce.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.dto.product.ProductRequestDTO;
import br.db.tec.e_commerce.dto.product.ProductResponseDTO;

public class ProductBuilder {

  private Long id = null;
  private String sku = "SKU-" + UUID.randomUUID().toString(); 
  private String name = "Teclado Mecânico";
  private final String description = "Teclado Mecanico generico com switch red omron.";
  private Long priceCents = 15000L;
  private String currency = "BRL";
  private Boolean active = true;
  private int stockQuantity = 10;
  private Category category = null;
  private Long categoryId;
  private OffsetDateTime createdAt=OffsetDateTime.now();
  private OffsetDateTime updatedAt=OffsetDateTime.now();

  public static ProductBuilder anProduct(){
    return new ProductBuilder();
  }
  public ProductBuilder withId(Long id){
    this.id = id;
    return this;
  }
  
  public ProductBuilder withSku(String sku){
    this.sku = sku;
    return this;
  }
  public ProductBuilder withName(String name){
    this.name = name;
    return this;
  }
  public ProductBuilder withPriceCents(Long priceCents){
    this.priceCents = priceCents;
    return this;
  }
  public ProductBuilder withCurrency(String currency){
    this.currency = currency;
    return this;
  }
  public ProductBuilder withActive(Boolean active){
    this.active = active;
    return this;
  }
  public ProductBuilder withStockQuantity(int stockQuantity){
    this.stockQuantity = stockQuantity;
    return this;
  }
  public ProductBuilder withCategory(Category category){
    this.category = category;
    return this;
  }
  public ProductBuilder withCategoryId(Long categoryId){
    this.categoryId = categoryId;
    return this;
  }
  public ProductBuilder withCreatedAt(OffsetDateTime createdAt){
    createdAt = OffsetDateTime.now();
    this.createdAt = createdAt;
    return this;
  }
  public ProductBuilder withUpdateddAt(OffsetDateTime updatedAt){
    updatedAt = OffsetDateTime.now();
    this.updatedAt = updatedAt;
    return this;
  }

  public Product buildProduct(){
    return new Product(
        this.id,
        this.sku,
        this.name,
        this.description,
        this.priceCents,
        this.currency,
        this.category,
        this.stockQuantity,
        this.active,
        this.createdAt,
        this.updatedAt
        );
  }
  public ProductResponseDTO buildProductResponseDTO(){
    return new ProductResponseDTO(
        this.id,
        this.sku, 
        this.name,
        this.priceCents,
        this.categoryId,
        this.stockQuantity, 
        this.currency, 
        this.active);
  }
  public ProductRequestDTO buildProductRequestDTO(){
    return new ProductRequestDTO(
        this.sku,
        this.name,
        this.categoryId,
        this.priceCents,
        this.currency,
        this.active,
        this.stockQuantity);
  }
  
}
