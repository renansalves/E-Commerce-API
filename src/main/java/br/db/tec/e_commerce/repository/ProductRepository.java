package br.db.tec.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.db.tec.e_commerce.domain.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

    Optional<Product> findByIdAndActive(Long id, Boolean active);

    Page<Product> findByActiveTrueAndPriceCentsBetween(long l, long m, Pageable unpaged);
  
}
