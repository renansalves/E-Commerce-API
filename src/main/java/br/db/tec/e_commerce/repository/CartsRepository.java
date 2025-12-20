package br.db.tec.e_commerce.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.cart.Carts;

public interface CartsRepository extends JpaRepository<Carts, Long>{

  Optional<Carts> findByUser_Id(Long userId); // O '_' indica ao JPA para entrar no objeto 'user' e buscar o 'id'

}
