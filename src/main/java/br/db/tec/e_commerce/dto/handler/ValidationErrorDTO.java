package br.db.tec.e_commerce.dto.handler;

public record ValidationErrorDTO(
    String field,
    String message
    ) {
}
