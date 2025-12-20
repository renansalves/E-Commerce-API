package br.db.tec.e_commerce.dto.user;

import br.db.tec.e_commerce.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterRequestDTO(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotNull UserRole role
) {}
