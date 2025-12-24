package br.db.tec.e_commerce.ControllerTest;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.db.tec.e_commerce.Builder.OrderBuilder;
import br.db.tec.e_commerce.domain.order.OrderStatus;
import br.db.tec.e_commerce.dto.order.OrderResponseDTO;
import br.db.tec.e_commerce.service.order.OrderService;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean // Substitui o Service real por um Mock no contexto do Spring
  private OrderService orderService;

  @Test
  @DisplayName("Deve retornar 201 ao realizar checkout com sucesso")
  @WithMockUser(username = "user@db.com")
  void shouldPerformCheckoutSuccessfully() throws Exception {
    OrderResponseDTO response = OrderBuilder.anOrder().buildResponseDTO();

    when(orderService.checkout()).thenReturn(response);

    mockMvc.perform(post("/api/orders/checkout")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  @DisplayName("Deve retornar 404 se o carrinho estiver vazio no checkout")
  @WithMockUser(username = "user@db.com")
  void shouldReturnErrorWhenCartIsEmpty() throws Exception {
    // Simulamos que o service lança uma exceção quando o carrinho está vazio
    when(orderService.checkout()).thenThrow(new EntityNotFoundException("Carrinho vazio"));

    mockMvc.perform(post("/api/orders/checkout")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Deve retornar detalhes de um pedido pertencente ao usuário logado")
  @WithMockUser(username = "user@db.com")
  void shouldReturnOrderDetailsForOwner() throws Exception {
    Long orderId = 10L;
    OrderResponseDTO response = OrderBuilder.anOrder().withId(orderId).buildResponseDTO();

    when(orderService.getOrderDetails(orderId)).thenReturn(response);

    mockMvc.perform(get("/api/orders/" + orderId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId));
  }

  @Test
  @DisplayName("Deve retornar 403 ou 400 ao tentar acessar pedido de outro usuário")
  @WithMockUser(username = "user@db.com")
  void shouldReturnErrorWhenAccessingOtherUserOrder() throws Exception {
    Long orderId = 99L;

    when(orderService.getOrderDetails(orderId))
        .thenThrow(new RuntimeException("Acesso negado"));

    mockMvc.perform(get("/api/orders/" + orderId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()); // Ou .isForbidden() dependendo do seu GlobalExceptionHandler
  }

  @Test
  @DisplayName("Admin deve conseguir alterar status do pedido")
  @WithMockUser(username = "admin@db.com", roles = "ADMIN")
  void adminShouldUpdateStatus() throws Exception {
    // Crie um DTO de resposta válido para evitar NullPointerException no Jackson
    OrderResponseDTO response = OrderBuilder.anOrder().buildResponseDTO();

    // Use eq() para garantir que o Mockito identifique os tipos corretamente
    when(orderService.updateOrderStatus(eq(1L), any(OrderStatus.class)))
        .thenReturn(response);

    mockMvc.perform(patch("/api/orders/admin/1/status")
        .param("status", "PAID") // Envia como query param: ?status=PAID
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  @DisplayName("Cliente comum não deve conseguir alterar status")
  @WithMockUser(username = "user@db.com", roles = "CLIENTE")
  void clientShouldNotUpdateStatus() throws Exception {
    mockMvc.perform(patch("/api/admin/orders/1/status")
        .param("status", "PAID"))
        .andExpect(status().isForbidden());
  }
}
