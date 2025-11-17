package com.exemplo.seguranca.api;

import com.exemplo.seguranca.modelo.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de registro de novo usuário
 */
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotNull(message = "Role é obrigatória")
    private Usuario.Role role;

    private Long restauranteId; // Obrigatório apenas para RESTAURANTE e ENTREGADOR

    public RegisterRequest() {
    }

    public RegisterRequest(String nome, String email, String senha, Usuario.Role role, Long restauranteId) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.restauranteId = restauranteId;
    }

    // Getters e Setters
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuario.Role getRole() {
        return role;
    }

    public void setRole(Usuario.Role role) {
        this.role = role;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }
}