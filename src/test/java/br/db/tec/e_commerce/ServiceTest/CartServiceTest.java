package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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

import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.domain.user.Users;
import br.db.tec.e_commerce.dto.cart.CartItemRequestDTO;
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
  @InjectMocks
  private CartService cartService;
  @InjectMocks
  private Users mockUser;

  @BeforeEach
  void setUp() {
    mockUser.setId(1L);
    mockUser.setEmail("cliente@db.com");

    Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, null);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);

    Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
  }

  

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext(); // Limpa para o próximo teste
  }

  @Test
  @DisplayName("Deve lançar exceção se o produto estiver inativo")
  void shouldThrowExceptionWhenProductInactive() {
    Long userId = 1L;
    CartItemRequestDTO dto = new CartItemRequestDTO(99L, 1);

    Product inactiveProduct = new Product();
    inactiveProduct.setActive(false);

    when(cartsRepository.findByUser_Id(userId)).thenReturn(Optional.of(new Carts()));
    when(productRepository.findById(99L)).thenReturn(Optional.of(inactiveProduct));

    assertThrows(EntityNotFoundException.class, () -> cartService.addItemToCart(dto));
  }
}
