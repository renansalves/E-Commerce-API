package br.db.tec.e_commerce.domain.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
    @NotBlank @Email String email,
    @NotBlank @Size(min=8) String password
    ) {
}
