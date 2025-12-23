package br.db.tec.e_commerce.dto.handler;

import java.time.OffsetDateTime;

public record ErrorResponseDTO(
    String message,
    String code,
    OffsetDateTime timestamp
    ) {
}
