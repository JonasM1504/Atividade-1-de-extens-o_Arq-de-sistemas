package com.exemplo.seguranca.dto;

import com.exemplo.seguranca.modelo.Usuario;
import java.time.LocalDateTime;

public record UserResponse(
    Long id, String nome, String email, Usuario.Role role, Long restauranteId, LocalDateTime dataCriacao
) {
    public static UserResponse fromEntity(Usuario usuario) {
        return new UserResponse(
            usuario.getId(), usuario.getNome(), usuario.getEmail(), 
            usuario.getRole(), usuario.getRestauranteId(), usuario.getDataCriacao()
        );
    }
}