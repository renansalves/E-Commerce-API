package br.db.tec.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

  List<CartItems> findByCart(Carts cart);

  Optional<CartItems> findByCartAndProduct(Carts cart,
      Product product);

  void deleteByCart_User_IdAndProduct_Id(Long userId, Long productId);

  void deleteByCart(Carts cart);

}
