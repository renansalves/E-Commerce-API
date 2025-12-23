package br.db.tec.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.category.Category;
import jakarta.validation.constraints.NotBlank;

public interface CategoryRepository extends JpaRepository<Category, Long>{

    boolean existsByName(@NotBlank(message = "O nome da categoria é obrigatório") String name);

    void deleteByName(String categoryName);


}
