package br.db.tec.e_commerce.dto.cart;

public record CartItemResponseDTO(
    Long itemId, // ID do CartItem para facilitar DELETE/PUT unitário
    Long productId,
    String productName,
    Integer quantity,
    Long unitPrice,
    Long subtotal // quantity * unitPrice
) {}
