
package br.db.tec.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.order.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUser_IdOrderByCreatedAtDesc(Long id);

}
