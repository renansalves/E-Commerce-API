package br.db.tec.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.OrderItems;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long>{

  
}
