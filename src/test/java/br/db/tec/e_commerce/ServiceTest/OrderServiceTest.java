package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.mapper.order.OrderMapper;
import br.db.tec.e_commerce.repository.CartItemsRepository;
import br.db.tec.e_commerce.repository.CartsRepository;
import br.db.tec.e_commerce.repository.OrderItemsRepository;
import br.db.tec.e_commerce.repository.OrdersRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.service.order.OrderService;

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
  private OrderItemsRepository orderItemsRepository;

  @InjectMocks
  private OrderService orderService;
  @InjectMocks
  private Users mockUser;

  @BeforeEach
  void setUp() {
    mockUser = new Users();
    mockUser.setId(1L);
    mockUser.setEmail("admin@db.com");
    mockUser.setRole(UserRole.ADMIN); 

    Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, null);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);

    Mockito.when(securityContext.getAuthentication()).thenReturn(auth);

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
}
