package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.UserRole;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.cart.CartItemRequestDTO;
import br.db.tec.e_commerce.dto.cart.CartResponseDTO;
import br.db.tec.e_commerce.mapper.cart.CartMapper;
import br.db.tec.e_commerce.repository.CartItemsRepository;
import br.db.tec.e_commerce.repository.CartsRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.service.cart.CartService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  private CartsRepository cartsRepository;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private CartItemsRepository cartItemsRepository;
  @Mock
  private CartMapper cartMapper;
  @InjectMocks
  private CartService cartService;

  private Users mockUser;

  @BeforeEach
  void setup() {
    mockUser = new Users();
    mockUser.setId(1L);
    mockUser.setEmail("test@user.com");
    mockUser.setPassword("test123");
    mockUser.setRole(UserRole.CLIENTE);

    Authentication auth = mock(Authentication.class);
    lenient().when(auth.getPrincipal()).thenReturn(mockUser);
    lenient().when(auth.isAuthenticated()).thenReturn(true);

    SecurityContext securityContext = mock(SecurityContext.class);
    lenient().when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Deve adicionar item com sucesso criando um novo carrinho se não existir")
  void shouldCreateNewCartAndAddItem() {
    CartItemRequestDTO dto = new CartItemRequestDTO(10L, 2);
    Product product = new Product();
    product.setId(10L);
    product.setActive(true);
    product.setStockQuantity(5);

    when(cartsRepository.findByUser_Id(1L)).thenReturn(Optional.empty());
    when(productRepository.findById(10L)).thenReturn(Optional.of(product));

    CartResponseDTO mockResponse = new CartResponseDTO(1L, new ArrayList<>(), 0L);
    when(cartMapper.toResponseDTO(any(), any())).thenReturn(mockResponse);

    CartResponseDTO result = cartService.addItemToCart(dto);

    assertNotNull(result);
    verify(cartsRepository, times(1)).save(any(Carts.class));
    verify(cartItemsRepository, times(1)).save(any(CartItems.class));
  }

  @Test
  @DisplayName("Deve lançar exceção se o produto estiver inativo")
  void shouldThrowExceptionWhenProductInactive() {
    CartItemRequestDTO dto = new CartItemRequestDTO(99L, 1);
    Product inactiveProduct = new Product();
    inactiveProduct.setActive(false);

    when(productRepository.findById(99L)).thenReturn(Optional.of(inactiveProduct));

    assertThrows(EntityNotFoundException.class, () -> cartService.addItemToCart(dto));
  }

  @Test
  @DisplayName("Deve lançar exceção se a quantidade solicitada for maior que o stock")
  void shouldThrowExceptionWhenInsufficientStock() {
    CartItemRequestDTO dto = new CartItemRequestDTO(10L, 100); 
    Product product = new Product();
    product.setId(10L);
    product.setActive(true);
    product.setStockQuantity(10); 

    when(productRepository.findById(10L)).thenReturn(Optional.of(product));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> cartService.addItemToCart(dto));

    assertTrue(exception.getMessage().contains("Estoque insuficiente"));
  }

}
