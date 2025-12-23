package br.db.tec.e_commerce.MapperTest;

import br.db.tec.e_commerce.domain.cart.CartItems;
import br.db.tec.e_commerce.domain.product.Product;
import br.db.tec.e_commerce.mapper.cart.CartMapper;
import br.db.tec.e_commerce.dto.cart.CartItemResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartMapperTest {

    @Autowired
    private CartMapper cartMapper;

    @Test
    @DisplayName("Deve mapear CartItems para CartItemResponseDTO com sucesso")
    void shouldMapCartItemsToResponseDTO() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto Teste");
        product.setPriceCents(1000L);

        CartItems item = new CartItems();
        item.setId(10L);
        item.setProduct(product);
        item.setQuantity(2);

        // Act
        CartItemResponseDTO dto = cartMapper.toItemResponseDTO(item);

        // Assert
        assertNotNull(dto);
        assertEquals(item.getId(), dto.itemId());
        assertEquals(product.getName(), dto.productName());
        assertEquals(item.getQuantity(), dto.quantity());
    }

    @Test
    @DisplayName("Deve retornar null ao mapear CartItems nulo")
    void shouldReturnNullWhenCartItemIsNull() {
        CartItemResponseDTO dto = cartMapper.toItemResponseDTO(null);
        assertNull(dto);
    }
}
