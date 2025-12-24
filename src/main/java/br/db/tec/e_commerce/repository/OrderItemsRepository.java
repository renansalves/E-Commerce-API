package br.db.tec.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.order.OrderItems;
import br.db.tec.e_commerce.domain.order.Orders;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long>{

    List<OrderItems> findByOrders(Orders order);

  
}
