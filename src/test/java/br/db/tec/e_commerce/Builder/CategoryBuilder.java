package br.db.tec.e_commerce.Builder;

import java.time.OffsetDateTime;

import br.db.tec.e_commerce.domain.category.Category;
import br.db.tec.e_commerce.dto.category.CategoryRequestDTO;
import br.db.tec.e_commerce.dto.category.CategoryResponseDTO;

public class CategoryBuilder {

  private Long id = 1L;
  private String name = "Perifericos";
  private String description = "Perifericos de informatica.";
  private OffsetDateTime createdAt;

  public static CategoryBuilder anCategory(){
    return new CategoryBuilder();
  }

  public CategoryBuilder withId(Long id){
    this.id = id;
    return this;
  }
  public CategoryBuilder withName(String name){
    this.name = name;
    return this;
  }
  public CategoryBuilder withDescription(String description){
    this.description = description;
    return this;
  }
  public CategoryBuilder withCreatedDate(OffsetDateTime createdAt){
    this.createdAt = createdAt;
    return this;
  }

  public Category buildCategory(){
    return new Category(this.id,this.name,this.description,this.createdAt);
  }
  public CategoryRequestDTO buildCategoryRequestDTO(){
    return new CategoryRequestDTO(this.name, this.description);
  }
  
  public CategoryResponseDTO buildCategoryResponseDTO(){
    return new CategoryResponseDTO(this.id,this.name, this.description);
  }
  
}
