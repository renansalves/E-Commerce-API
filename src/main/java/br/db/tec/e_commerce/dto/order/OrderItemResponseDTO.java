package br.db.tec.e_commerce.dto.order;

public record OrderItemResponseDTO(
    Long productId,
    String productName,
    Integer quantity,
    Long unitPriceSnapshot // Preço fixado no momento do checkout
) {}
