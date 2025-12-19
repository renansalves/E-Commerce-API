package br.db.tec.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{


}
