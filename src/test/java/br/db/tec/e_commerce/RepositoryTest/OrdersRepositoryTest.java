package br.db.tec.e_commerce.RepositoryTest;

import br.db.tec.e_commerce.domain.order.OrderStatus;
import br.db.tec.e_commerce.domain.order.Orders;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.repository.OrdersRepository;
import br.db.tec.e_commerce.domain.user.UserRole; 
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrdersRepositoryTest {

  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired
  private TestEntityManager entityManager; 

  @Test
  @DisplayName("Should save order linked to a user")
  void shouldSaveOrder() {
    Users user = new Users(
        null,
        "test@test.com",
        "hashed_pass",
        UserRole.CLIENTE,
        true,
        OffsetDateTime.now());

    Users savedUser = entityManager.persistAndFlush(user);

    Orders order = new Orders();
    order.setUser(savedUser);
    order.setTotalCents(15000L);
    order.setOrderStatus(OrderStatus.PENDING);
    order.setCreatedAt(OffsetDateTime.now());

    Orders savedOrder = ordersRepository.save(order);

    assertThat(savedOrder.getId()).isNotNull();
    assertThat(savedOrder.getUser()).isEqualTo(savedUser);
    assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
  }
}
