package br.db.tec.e_commerce.controller;

import br.db.tec.e_commerce.domain.order.OrderStatus;
import br.db.tec.e_commerce.dto.order.OrderResponseDTO;
import br.db.tec.e_commerce.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Endpoints para gerenciamento de pedidos e checkout")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/checkout")
  @Operation(summary = "Finaliza o checkout", description = "Converte o carrinho do usuário atual em um pedido e baixa o estoque.")
  public ResponseEntity<OrderResponseDTO> checkout() {
    OrderResponseDTO response = orderService.checkout();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Busca pedido por ID", description = "Retorna os detalhes de um pedido específico.")
  public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderDetails(id));
  }

  @GetMapping
  @Operation(summary = "Lista pedidos do usuário", description = "Retorna o histórico de pedidos do usuário autenticado.")
  public ResponseEntity<List<OrderResponseDTO>> listMyOrders() {
    return ResponseEntity.ok(orderService.listMyOrders());
  }

  @PatchMapping("/admin/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Atualiza status do pedido", description = "Acesso restrito a administradores.")
  public ResponseEntity<OrderResponseDTO> updateStatus(
      @PathVariable Long id,
      @RequestParam OrderStatus status) {
    return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
  }
}
