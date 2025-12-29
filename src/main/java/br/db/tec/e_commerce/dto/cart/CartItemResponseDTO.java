package br.db.tec.e_commerce.dto.cart;

public record CartItemResponseDTO(
    Long itemId, 
    Long productId,
    String productName,
    Integer quantity,
    Long unitPrice,
    Long subtotal 
) {}
