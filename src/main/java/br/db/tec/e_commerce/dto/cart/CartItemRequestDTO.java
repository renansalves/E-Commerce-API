package br.db.tec.e_commerce.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequestDTO(
    @NotNull Long productId,
    @NotNull @Min(1) Integer quantity
) {}
