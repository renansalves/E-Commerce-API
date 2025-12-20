package br.db.tec.e_commerce.dto.product;

public record ProductResponseDTO(
    Long id,
    String sku,
    String name,
    String description,
    Long priceCents,
    Integer stockQuantity,
    String currency,
    Boolean active
) {}
