package br.db.tec.e_commerce.dto.product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

// Entrada: Criação e Atualização (ADMIN)
public record ProductRequestDTO(
    @NotBlank String sku,
    @NotBlank String name,
    String description,
    @NotNull @Min(0) Long priceCents,
    @NotBlank String currency, // Default "BRL" no banco
    @NotNull Boolean active,
    @Min(0) Integer stockQuantity
) {}

