package br.db.tec.e_commerce.dto.product;

public record ProductResponseDTO(
    Long id,
    String sku,
    String name,
    Long priceCents,
    Long categoryId, 
    Integer stockQuantity,
    String currency,
    Boolean active
) {}
