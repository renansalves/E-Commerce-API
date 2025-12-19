package br.db.tec.e_commerce.repository;

import br.db.tec.e_commerce.domain.*;
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
        // 1. Setup User
        Users user = new Users();
        user.setEmail("cartuser@test.com");
        user.setPasswordHash("123");
        user.setRole(UserRole.CLIENTE);
        user.setEnabled(true);
        user.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(user);

        // 2. Setup Cart
        Carts cart = new Carts();
        cart.setUserId(user);
        cart.setCreatedAt(OffsetDateTime.now());
        cart.setUpdatedAt(OffsetDateTime.now());
        entityManager.persist(cart);

        // 3. Setup Product
        Product product = new Product();
        product.setSku("CART-PROD-01");
        product.setName("Mouse");
        product.setPriceCents(1000L);
        product.setActive(true);
        product.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(product);

        entityManager.flush();

        // 4. Teste: Criar Item
        CartItems item = new CartItems();
        item.setCarts(cart);
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(1000.0); // Nota: Sua entidade usa Double, SQL usa BIGINT. Cuidado com conversão.
        item.setCreatedAt(OffsetDateTime.now());

        CartItems savedItem = cartItemsRepository.save(item);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getCarts().getId()).isEqualTo(cart.getId());
        assertThat(savedItem.getProduct().getSku()).isEqualTo("CART-PROD-01");
    }
}
