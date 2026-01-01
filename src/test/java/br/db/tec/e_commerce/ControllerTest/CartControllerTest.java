package br.db.tec.e_commerce.ControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.db.tec.e_commerce.dto.cart.CartItemRequestDTO;
import br.db.tec.e_commerce.dto.cart.CartResponseDTO;
import br.db.tec.e_commerce.service.cart.CartService;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve retornar 201 ao adicionar item no carrinho")
    void shouldReturnCreatedWhenAddItem() throws Exception {
        CartItemRequestDTO request = new CartItemRequestDTO(1L, 2);
        CartResponseDTO response = new CartResponseDTO(1L, List.of(), 1000L);

        when(cartService.addItemToCart(any())).thenReturn(response);

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1, \"quantity\": 2}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 200 ao buscar o carrinho do usuário")
    void shouldReturnOkWhenGettingMyCart() throws Exception {
        CartResponseDTO response = new CartResponseDTO(1L, List.of(), 0L);
        when(cartService.getCurrentCart()).thenReturn(response);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 204 ao limpar o carrinho")
    void shouldReturnNoContentWhenClearCart() throws Exception {
        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart();
    }
}
