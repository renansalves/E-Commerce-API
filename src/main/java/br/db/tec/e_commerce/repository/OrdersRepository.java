
package br.db.tec.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.db.tec.e_commerce.domain.Orders;
public interface OrdersRepository extends JpaRepository<Orders, Long>{

  
}
