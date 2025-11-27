package com.exemplo.seguranca.modelo;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    
    public enum Role { CLIENTE, RESTAURANTE, ADMIN, ENTREGADOR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String senha; 
    
    private String nome;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private Long restauranteId; // Usado para ROLE.RESTAURANTE
    private Boolean ativo = true;
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // --- Implementação UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // A autoridade é registrada como "ROLE_NOME_DO_PERFIL"
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override public String getPassword() { return this.senha; }
    @Override public String getUsername() { return this.email; }
    // Métodos de estado (True padrão)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return this.ativo; }
    
    // --- Getters e Setters (Omitidos aqui, mas devem estar no seu código!) ---
}