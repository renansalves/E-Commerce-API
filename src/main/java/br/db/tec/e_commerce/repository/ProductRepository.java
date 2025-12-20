package br.db.tec.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.db.tec.e_commerce.domain.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
  
}
