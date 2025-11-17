package com.exemplo.seguranca.modelo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Entidade de usuário que implementa UserDetails do Spring Security
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    /**
     * Enum para as roles/papéis do usuário
     */
    public enum Role {
        CLIENTE, RESTAURANTE, ADMIN, ENTREGADOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    /**
     * ID do restaurante associado ao usuário.
     * Obrigatório apenas para usuários com role RESTAURANTE e ENTREGADOR.
     */
    private Long restauranteId;

    // --- Implementação da Interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }

    /**
     * Valida se a role exige um restauranteId
     */
    @PrePersist
    @PreUpdate
    private void validarRestauranteId() {
        if ((role == Role.RESTAURANTE || role == Role.ENTREGADOR) && restauranteId == null) {
            throw new IllegalArgumentException(
                "restauranteId é obrigatório para usuários com role " + role.name()
            );
        }
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", role=" + role +
                ", ativo=" + ativo +
                ", dataCriacao=" + dataCriacao +
                ", restauranteId=" + restauranteId +
                '}';
    }
}