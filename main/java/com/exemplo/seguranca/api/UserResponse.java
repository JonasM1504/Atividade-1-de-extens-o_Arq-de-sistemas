package com.exemplo.seguranca.api;

import com.exemplo.seguranca.modelo.Usuario;
import java.time.LocalDateTime;

/**
 * DTO para resposta com dados do usuário (sem expor a senha)
 */
public class UserResponse {

    private Long id;
    private String nome;
    private String email;
    private String role;
    private Long restauranteId;
    private LocalDateTime dataCriacao;

    public UserResponse() {
    }

    public UserResponse(Long id, String nome, String email, String role, Long restauranteId, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.restauranteId = restauranteId;
        this.dataCriacao = dataCriacao;
    }

    /**
     * Factory method para converter Usuario em UserResponse
     */
    public static UserResponse fromEntity(Usuario usuario) {
        return new UserResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().name(),
            usuario.getRestauranteId(),
            usuario.getDataCriacao()
        );
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}