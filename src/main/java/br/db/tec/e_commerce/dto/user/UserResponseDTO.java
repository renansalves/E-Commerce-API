package br.db.tec.e_commerce.dto.user;

import br.db.tec.e_commerce.domain.user.UserRole;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    UserRole role,
    Boolean enabled) {
}
