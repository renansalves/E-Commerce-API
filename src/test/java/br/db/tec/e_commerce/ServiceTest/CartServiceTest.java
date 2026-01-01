package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
      inactiveProduct.setId(99L);
      inactiveProduct.setActive(false);

      when(productRepository.findById(99L)).thenReturn(Optional.of(inactiveProduct));

      EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
              () -> cartService.addItemToCart(dto));

      assertEquals("Produto não encontrado ou inativo", exception.getMessage());

      verify(cartItemsRepository, times(0)).save(any());
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

      verify(cartItemsRepository, times(0)).save(any(CartItems.class));
      assertTrue(exception.getMessage().contains("Estoque insuficiente"));
  }
    @Test
    @DisplayName("Deve incrementar a quantidade de um item se ele já existir no carrinho")
    void shouldIncrementQuantityWhenItemAlreadyInCart() {
        CartItemRequestDTO dto = new CartItemRequestDTO(10L, 2);
        Product product = new Product();
        product.setId(10L);
        product.setActive(true);
        product.setStockQuantity(10);
        product.setPriceCents(1000L);

        Carts cart = new Carts();
        CartItems existingItem = new CartItems();
        existingItem.setId(1L);
        existingItem.setQuantity(3);
        existingItem.setProduct(product);

        when(cartsRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartsAndProduct(cart, product)).thenReturn(Optional.of(existingItem));

        // Configurando o retorno do Record para evitar NullPointerException
        CartResponseDTO mockResponse = new CartResponseDTO(1L, new ArrayList<>(), 5000L);
        when(cartMapper.toResponseDTO(any(), any())).thenReturn(mockResponse);

        cartService.addItemToCart(dto);

        assertEquals(5, existingItem.getQuantity()); // 3 pré-existentes + 2 do DTO
        verify(cartItemsRepository).save(existingItem);
    }
    @Test
    @DisplayName("Deve retornar detalhes de um carrinho específico por ID")
    void shouldGetCartDetailed() {
        Carts cart = new Carts();
        cart.setId(1L);
        when(cartsRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findByCarts(cart)).thenReturn(new ArrayList<>());

        CartResponseDTO mockResponse = new CartResponseDTO(1L, new ArrayList<>(), 0L);
        when(cartMapper.toResponseDTO(any(), any())).thenReturn(mockResponse);

        CartResponseDTO result = cartService.getCartDetailed(1L);

        assertNotNull(result);
        verify(cartsRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar o carrinho do usuário autenticado")
    void shouldGetCurrentCart() {
        Carts cart = new Carts();
        when(cartsRepository.findByUser_Id(mockUser.getId())).thenReturn(Optional.of(cart));

        CartResponseDTO mockResponse = new CartResponseDTO(1L, new ArrayList<>(), 0L);
        when(cartMapper.toResponseDTO(any(), any())).thenReturn(mockResponse);

        CartResponseDTO result = cartService.getCurrentCart();

        assertNotNull(result);
    }
    @Test
    @DisplayName("Deve limpar todos os itens do carrinho do usuário")
    void shouldClearCart() {
        Carts cart = new Carts();
        when(cartsRepository.findByUser_Id(mockUser.getId())).thenReturn(Optional.of(cart));

        cartService.clearCart();

        verify(cartItemsRepository).deleteByCarts(cart);
    }

    @Test
    @DisplayName("Deve remover um item específico do carrinho")
    void shouldRemoveItemFromCart() {
        Long productId = 10L;

        cartService.removeItemFromCart(productId);

        verify(cartItemsRepository).deleteByCarts_User_IdAndProduct_Id(mockUser.getId(), productId);
    }
    @Test
    @DisplayName("Deve lançar exceção ao buscar carrinho atual de usuário sem carrinho")
    void shouldThrowExceptionWhenUserHasNoCart() {
        // mockUser.getId() é 1L conforme seu setUp
        when(cartsRepository.findByUser_Id(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> cartService.getCurrentCart());

        assertEquals("Carrinho vazio ou não encontrado", ex.getMessage());
    }
}
