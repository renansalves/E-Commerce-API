package br.db.tec.e_commerce.RepositoryTest;

import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.repository.CartItemsRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartItemsRepositoryTest {

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save cart item with relationships")
    void shouldSaveCartItem() {
        Users user = new Users();
        user.setEmail("cartuser@test.com");
        user.setPassword("123");
        user.setRole(UserRole.CLIENTE);
        user.setEnabled(true);
        user.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(user);

        Carts cart = new Carts();
        cart.setUser(user);
        cart.setCreatedAt(OffsetDateTime.now());
        cart.setUpdatedAt(OffsetDateTime.now());
        entityManager.persist(cart);

        Product product = new Product();
        product.setSku("CART-PROD-01");
        product.setName("Mouse");
        product.setPriceCents(1000L);
        product.setActive(true);
        product.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(product);

        entityManager.flush();

        CartItems item = new CartItems();
        item.setCarts(cart);
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(1000L); 
        item.setCreatedAt(OffsetDateTime.now());

        CartItems savedItem = cartItemsRepository.save(item);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getCarts().getId()).isEqualTo(cart.getId());
        assertThat(savedItem.getProduct().getSku()).isEqualTo("CART-PROD-01");
    }
}
