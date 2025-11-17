// Arquivo: src/test/java/com/exemplo/seguranca/api/ApiSegurancaApplicationTests.java

package com.exemplo.seguranca.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Carrega o contexto Spring Boot
@SpringBootTest
// 2. Configura e injeta o MockMvc para simular requisições HTTP
@AutoConfigureMockMvc 
class ApiSegurancaApplicationTests {

    @Autowired
    private MockMvc mockMvc; // Objeto para simular requisições HTTP

    // Teste 1: Endpoints Públicos (Sem autenticação)
    // O endpoint /api/restaurantes foi configurado para .permitAll()
    @Test
    void testAcessoEndpointPublico() throws Exception {
        mockMvc.perform(get("/api/restaurantes"))
               .andExpect(status().isOk()); // Deve retornar HTTP 200 OK
    }

    // --- TESTES DE ACESSO PROTEGIDO ---
    
    // Teste 2: Endpoints Protegidos (Acesso negado)
    // Se não houver autenticação, qualquer outro endpoint deve falhar (401 Unauthorized)
    @Test
    void testAcessoEndpointProtegidoNaoAutorizado() throws Exception {
        // Usamos um endpoint que não foi listado no .permitAll() (ex: /api/admin/recurso)
        mockMvc.perform(get("/api/admin/recurso"))
               .andExpect(status().isUnauthorized()); // Deve retornar 401 Unauthorized
    }
    
    // Teste 3: Endpoints Protegidos (Acesso com usuário Mock)
    // Simula um usuário autenticado com a ROLE ADMIN
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAcessoEndpointProtegidoAutorizado() throws Exception {
        // O Spring Security simulará que um usuário ADMIN está logado
        mockMvc.perform(get("/api/admin/recurso"))
               .andExpect(status().isOk()); // Se o ADMIN tiver permissão, deve retornar 200 OK
    }
}