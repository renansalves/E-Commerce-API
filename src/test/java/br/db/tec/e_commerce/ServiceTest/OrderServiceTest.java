package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.order.OrderItems;
import br.db.tec.e_commerce.domain.order.OrderStatus;
import br.db.tec.e_commerce.domain.order.Orders;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.order.OrderResponseDTO;
import br.db.tec.e_commerce.exception.CheckoutException;
import br.db.tec.e_commerce.mapper.cart.CartMapper;
import br.db.tec.e_commerce.mapper.order.OrderMapper;
import br.db.tec.e_commerce.repository.CartItemsRepository;
import br.db.tec.e_commerce.repository.CartsRepository;
import br.db.tec.e_commerce.repository.OrderItemsRepository;
import br.db.tec.e_commerce.repository.OrdersRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.service.cart.CartService;
import br.db.tec.e_commerce.service.order.OrderService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

  @Mock
  private OrdersRepository ordersRepository;
  @Mock
  private CartItemsRepository cartItemsRepository;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private CartsRepository cartsRepository;
  @Mock
  private OrderMapper orderMapper;
  @Mock
  CartMapper cartMapper;
  @Mock
  private OrderItemsRepository orderItemsRepository;

  @InjectMocks
  private OrderService orderService;

  private Users mockUser;

  @BeforeEach
  void setUp() {
    mockUser = new Users();
    mockUser.setId(1L);
    mockUser.setEmail("admin@db.com");
    mockUser.setRole(UserRole.ADMIN);
    mockUser.setPassword("123456789");

    Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, null);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(auth);

    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Deve finalizar checkout com sucesso e baixar stock")
  void checkoutSuccess() {
    Long userId = 1L;
    Users user = new Users();
    Carts cart = new Carts();
    cart.setUser(user);

    Product p = new Product();
    p.setStockQuantity(10);
    p.setPriceCents(1000L);

    CartItems item = new CartItems();
    item.setProduct(p);
    item.setQuantity(2);
    item.setUnitPrice(1000L);

    when(cartsRepository.findByUser_Id(userId)).thenReturn(Optional.of(cart));
    when(cartItemsRepository.findByCarts(cart)).thenReturn(List.of(item));
    when(ordersRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

    orderService.checkout();

    assertEquals(8, p.getStockQuantity());
    verify(orderItemsRepository, times(1)).saveAll(anyList());
    verify(cartItemsRepository, times(1)).deleteAll(anyList());
  }

  @Test
  @DisplayName("Deve receber uma exceção ao tentar fazer checkout com carrinho vazio.")
  void ShouldThrowAnExceptionWhenTryToCheckoutANullCart() {

    Long userId = 1L;
    Users user = new Users();
    Carts cart = new Carts();
    String faultMessage = "Carrinho não encontrado para este usuário";
    cart.setUser(user);

    Product p = new Product();
    p.setStockQuantity(10);
    p.setPriceCents(1000L);

    CartItems item = new CartItems();

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> orderService.checkout());

    assertEquals(exception.getMessage(), faultMessage);
  }

  @Test
  @DisplayName("Deve retornar erro ao buscar pedido inexistente.")
  void ShoultThrowExceptionWhenTryToObtainAnOrderDetailsWithAnInvalidId() {
    Long id = 99L;
    String expected = String.format("Pedido não encontrado com o ID: " + id);
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> orderService.getOrderDetails(id));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  @DisplayName("Deve lançar uma exceção quando for realizado um checkout sem carrinho.")
  void shouldThrowExceptionWhenCheckoutWithEmptyCart() {
    Users user = new Users();
    Carts cart = new Carts();
    user.setId(1L);
    user.setEmail("user@user.com");
    user.setRole(UserRole.CLIENTE);
    user.setPassword("123456789");

    cart.setUser(user);

    when(cartsRepository.findByUser_Id(user.getId())).thenReturn(Optional.of(cart));
    when(cartItemsRepository.findByCarts(cart)).thenReturn(Collections.emptyList());

    CheckoutException ex = assertThrows(CheckoutException.class, () -> orderService.checkout());
    assertEquals("Não é possível finalizar um pedido com o carrinho vazio", ex.getMessage());
  }

  @Test
  @DisplayName("Não permite acessar pedidos de outros usuários.")
  void shouldThrowExceptionWhenAccessingOrderOfAnotherUser() {
    Users loggedUser = new Users();
    Users ownerUser = new Users();
    loggedUser.setId(1L);
    ownerUser.setId(2L);
    Orders order = new Orders();
    order.setUser(ownerUser); // Pedido pertence a outro

    when(ordersRepository.findById(anyLong())).thenReturn(Optional.of(order));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.getOrderDetails(1L));
    assertEquals("Acesso negado: Você não tem permissão para visualizar este pedido.", ex.getMessage());
  }

  @Test
  @DisplayName("Não deve atualizar um pedido com status cancelado.")
  void shouldNotUpdateStatusOfCancelledOrder() {
    Orders order = new Orders();
    order.setOrderStatus(OrderStatus.CANCELLED);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> orderService.updateOrderStatus(1L, OrderStatus.DELIVERED));
    assertEquals("Não é possível alterar o status de um pedido cancelado.", ex.getMessage());
  }


  @Test
  @DisplayName("Deve lançar exceção quando usuário tenta acessar pedido de outro")
  void getOrderDetailsAccessDenied() {
    Users otherUser = new Users();
    otherUser.setId(99L);

    Orders order = new Orders();
    order.setId(1L);
    order.setUser(otherUser);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> orderService.getOrderDetails(1L));

    assertEquals("Acesso negado: Você não tem permissão para visualizar este pedido.", ex.getMessage());
  }

  @Test
  @DisplayName("Deve retornar detalhes do pedido quando o usuário for o dono")
  void getOrderDetailsSuccess() {
      Orders order1 = new Orders();
      order1.setId(10L);
      order1.setUser(mockUser);

      OrderResponseDTO responseDTO = new OrderResponseDTO(
              10L,
              OrderStatus.PENDING,
              2000L,
              OffsetDateTime.now(),
              Collections.emptyList()
      );

      when(ordersRepository.findByUser_IdOrderByCreatedAtDesc(mockUser.getId()))
              .thenReturn(List.of(order1));

      when(orderMapper.toResponseDTO(any(Orders.class), anyList()))
              .thenReturn(responseDTO);

      List<OrderResponseDTO> result = orderService.listMyOrders();

      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals(10L, result.get(0).id());

  }

  @Test
  @DisplayName("Deve listar apenas os pedidos do usuário autenticado")
  void listMyOrdersSuccess() {
    Orders order1 = new Orders();
    order1.setUser(mockUser);

    when(ordersRepository.findByUser_IdOrderByCreatedAtDesc(mockUser.getId()))
        .thenReturn(List.of(order1));
    when(orderItemsRepository.findByOrders(any())).thenReturn(Collections.emptyList());

    List<OrderResponseDTO> result = orderService.listMyOrders();

    assertEquals(1, result.size());
    verify(ordersRepository).findByUser_IdOrderByCreatedAtDesc(mockUser.getId());
  }
}
