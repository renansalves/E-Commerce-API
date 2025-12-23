package br.db.tec.e_commerce.controller;

import br.db.tec.e_commerce.dto.cart.CartItemRequestDTO;
import br.db.tec.e_commerce.dto.cart.CartResponseDTO;
import br.db.tec.e_commerce.service.cart.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/items")
    @Operation(
    summary = "Adiciona produto",
    description = "Adiciona um produto ao carrinho do utilizador logado"
    )
    public ResponseEntity<CartResponseDTO> addItem(@RequestBody @Valid CartItemRequestDTO dto) {
        CartResponseDTO response = cartService.addItemToCart(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
    summary = "Buscar Carrinho usuario",
    description = "Recupera o carrinho completo do utilizador logado"
    )
    public ResponseEntity<CartResponseDTO> getMyCart() {
        CartResponseDTO response = cartService.getCurrentCart();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long productId) {
        // Precisas de implementar este método no CartService
        cartService.removeItemFromCart(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
