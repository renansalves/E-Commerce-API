package br.db.tec.e_commerce.dto.cart;

import java.util.List;

public record CartResponseDTO(
    Long id,
    List<CartItemResponseDTO> items,
    Long totalCents
    ) {
}
