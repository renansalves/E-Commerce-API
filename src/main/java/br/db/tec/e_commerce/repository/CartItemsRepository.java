package br.db.tec.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;

public interface CartItemsRepository extends JpaRepository<CartItems, Long>{

    List<CartItems> findByCarts(Carts cart);

    Optional <CartItems> findByCartsAndProduct(Carts cart,
            Product product);


}
