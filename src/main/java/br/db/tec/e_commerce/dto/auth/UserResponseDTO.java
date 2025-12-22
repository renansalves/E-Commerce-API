package br.db.tec.e_commerce.dto.auth;

import br.db.tec.e_commerce.domain.user.UserRole;

public record UserResponseDTO(
    Long id,
    String email,
    UserRole role,
    Boolean enabled

    ) {
}
