package com.exemplo.seguranca.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBoot;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Testes da aplicação Spring Boot de Segurança
 */
@SpringBoot
public class ApiSegurancaApplicationTests {

    @Test
    public void contextLoads() {
        // Testa se o contexto Spring carrega corretamente
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAutenticacaoComAdmin() {
        // Testa autenticação com role ADMIN
    }
}