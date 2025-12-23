package br.db.tec.e_commerce.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
    @NotBlank(message = "O nome da categoria é obrigatório")
    String name,
    String description
){}

