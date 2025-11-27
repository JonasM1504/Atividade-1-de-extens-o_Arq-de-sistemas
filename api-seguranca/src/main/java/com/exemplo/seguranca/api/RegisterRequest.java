package com.exemplo.seguranca.dto;

import com.exemplo.seguranca.modelo.Usuario;
import jakarta.validation.constraints.*;

public record RegisterRequest(
    @NotBlank String nome,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6) String senha,
    @NotNull Usuario.Role role,
    Long restauranteId
) {}