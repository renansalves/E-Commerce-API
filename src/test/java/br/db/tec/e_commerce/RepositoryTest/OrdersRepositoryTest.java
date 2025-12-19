package br.db.tec.e_commerce.repository;

import br.db.tec.e_commerce.domain.OrderStatus;
import br.db.tec.e_commerce.domain.Orders;
import br.db.tec.e_commerce.domain.Users; // Assumindo existência
import br.db.tec.e_commerce.domain.UserRole; // Assumindo existência
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrdersRepositoryTest {

  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired
  private TestEntityManager entityManager; // Útil para configurar dados de setup

  @Test
  @DisplayName("Should save order linked to a user")
  void shouldSaveOrder() {
    // 1. Cenário: Precisamos de um User persistido primeiro
    Users user = new Users(
        null,
        "test@test.com",
        "hashed_pass",
        UserRole.CLIENTE,
        true,
        OffsetDateTime.now());

    Users savedUser = entityManager.persistAndFlush(user);

    // 2. Ação: Criar o Pedido
    Orders order = new Orders();
    order.setUser(savedUser);
    order.setTotalCents(15000L);
    order.setOrderStatus(OrderStatus.PENDING);
    order.setCreatedAt(OffsetDateTime.now());

    Orders savedOrder = ordersRepository.save(order);

    // 3. Verificação
    assertThat(savedOrder.getId()).isNotNull();
    assertThat(savedOrder.getUser()).isEqualTo(savedUser);
    assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
  }
}
