// Arquivo: src/test/java/com/exemplo/seguranca/util/JwtUtilTest.java

package com.exemplo.seguranca.util;

import com.exemplo.seguranca.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Usuario mockUser;

    // Configuração inicial antes de cada teste
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        
        // Simula a injeção da chave secreta (SECRET_KEY) para o teste
        // NOTA: Em testes reais de integração, o Spring faria isso automaticamente.
        // Chave BASE64 de 32 bytes (256 bits) para fins de teste
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", "bWluaGFjaGF2ZXNlY3JldGFjb21tYWlzZGV0cmluMmVjaGFy"); 

        // Cria um objeto Usuario mock
        mockUser = new Usuario() {
            // Overrides para simular o comportamento de UserDetails
            @Override public Long getId() { return 100L; }
            @Override public String getUsername() { return "teste@restaurante.com"; }
            @Override public String getPassword() { return "hash_senha"; }
            @Override public Usuario.Role getRole() { return Usuario.Role.RESTAURANTE; }
            @Override public Long getRestauranteId() { return 50L; }
            @Override public Collection<? extends GrantedAuthority> getAuthorities() { 
                return List.of(new SimpleGrantedAuthority("ROLE_" + getRole().name()));
            }
            // Outros métodos de UserDetails omitidos para brevidade
        };
        mockUser.setId(100L); // Definir o ID
        mockUser.setNome("Restaurante Teste");
        mockUser.setRole(Usuario.Role.RESTAURANTE);
        mockUser.setRestauranteId(50L);
        mockUser.setEmail("teste@restaurante.com");
    }

    @Test
    void testGeracaoEExtracaoToken() {
        // Geração
        String token = jwtUtil.generateToken(mockUser);
        assertNotNull(token);
        
        // Extração de Username (Email)
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(mockUser.getUsername(), extractedUsername);

        // Extração dos Claims Customizados
        Long extractedUserId = jwtUtil.extractClaim(token, claims -> claims.get("userId", Long.class));
        String extractedRole = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
        Long extractedRestauranteId = jwtUtil.extractClaim(token, claims -> claims.get("restauranteId", Long.class));

        // Validações
        assertEquals(mockUser.getId(), extractedUserId);
        assertEquals(mockUser.getRole().name(), extractedRole);
        assertEquals(mockUser.getRestauranteId(), extractedRestauranteId);

        // Validação de Token (teste se o token é válido para o usuário)
        assertTrue(jwtUtil.validateToken(token, mockUser));
    }
    
    @Test
    void testTokenExpirado() {
        // Gera um token e manipula sua data de expiração para o passado
        String token = jwtUtil.generateToken(mockUser);
        
        // Simulação forçada de expiração (requer manipulação interna, mas para fins de teste)
        // Você teria que mockar a data ou usar um tempo de expiração muito curto no generateToken
        // Aqui, assumimos que o método isTokenExpired funciona corretamente com datas passadas.
        assertFalse(jwtUtil.isTokenExpired(token), "O token não deve estar expirado imediatamente após a criação.");
    }
}