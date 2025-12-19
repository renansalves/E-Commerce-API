package br.db.tec.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long>{

  
}
