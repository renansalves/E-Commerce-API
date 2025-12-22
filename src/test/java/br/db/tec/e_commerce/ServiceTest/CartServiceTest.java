package br.db.tec.e_commerce.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import br.db.tec.e_commerce.domain.cart.Carts;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.dto.cart.CartItemRequestDTO;
import br.db.tec.e_commerce.repository.CartItemsRepository;
import br.db.tec.e_commerce.repository.CartsRepository;
import br.db.tec.e_commerce.repository.ProductRepository;
import br.db.tec.e_commerce.service.cart.CartService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartsRepository cartsRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartItemsRepository cartItemsRepository;
    @InjectMocks private CartService cartService;

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
