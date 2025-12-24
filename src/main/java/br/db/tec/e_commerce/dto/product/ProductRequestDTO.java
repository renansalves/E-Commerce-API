package br.db.tec.e_commerce.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Min;

public record ProductRequestDTO(
    @NotBlank String sku,
    @NotBlank String name,
    @NotNull Long categoryId,
    @NotNull @PositiveOrZero Long priceCents,
    @NotBlank String currency,
    @NotNull Boolean active,
    @Min(0) Integer stockQuantity) {
}
