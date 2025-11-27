package com.exemplo.seguranca.controlador;

import com.exemplo.seguranca.TesteIntegracaoBase;
import com.exemplo.seguranca.modelo.Usuario; // Usaremos Usuario como modelo para Cliente mock
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ClienteControllerIT extends TesteIntegracaoBase {

    private static final String CLIENTE_API = "/api/clientes";

    @Autowired
    private ObjectMapper objectMapper; // Usado para converter objetos Java em JSON

    // --- Dados Mock de Requisição (DTOs) ---
    private String criarClientePayload(String nome, String email) {
        return String.format("{\"nome\": \"%s\", \"email\": \"%s\", \"senha\": \"123456\", \"role\": \"CLIENTE\"}", nome, email);
    }
    
    // --- TESTAR POST /api/clientes (Criação) ---

    @Test
    @WithMockUser(roles = "ADMIN") // Apenas ADMIN pode criar novos clientes neste cenário
    void testCriarCliente_DadosValidos_Retorna201() throws Exception {
        String payload = criarClientePayload("Novo Cliente", "novo@teste.com");

        mockMvc.perform(post(CLIENTE_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.nome").value("Novo Cliente"))
                .andExpect(jsonPath("$.role").value(Usuario.Role.CLIENTE.name()))
                .andExpect(jsonPath("$.id").exists()); // Garante que foi persistido
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCriarCliente_DadosInvalidos_Retorna400() throws Exception {
        // Payload inválido: Email mal formatado e senha muito curta (assumindo validação @Email e @Size min=6)
        String payload = "{\"nome\": \"Cliente Invalido\", \"email\": \"emailinvalido\", \"senha\": \"123\"}";

        mockMvc.perform(post(CLIENTE_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.mensagem").exists()); // Espera uma mensagem de erro de validação
    }
    
    // --- TESTES AUXILIARES ---
    
    // Método auxiliar para criar e retornar o ID do cliente
    private Long criarClienteNaBase(String nome, String email) throws Exception {
        String payload = criarClientePayload(nome, email);
        
        // Simula o registro e captura o resultado
        MvcResult result = mockMvc.perform(post("/api/auth/register") // Usando o AuthController para registro
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn();
        
        // Extrai o ID do JSON de resposta (UserResponse)
        Usuario createdUser = objectMapper.readValue(result.getResponse().getContentAsString(), Usuario.class);
        return createdUser.getId();
    }
    
    // --- TESTAR GET /api/clientes/{id} (Busca) ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBuscarCliente_Existente_Retorna200() throws Exception {
        Long clienteId = criarClienteNaBase("Cliente Busca", "busca@teste.com");
        
        mockMvc.perform(get(CLIENTE_API + "/" + clienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(clienteId))
                .andExpect(jsonPath("$.nome").value("Cliente Busca"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testBuscarCliente_Inexistente_Retorna404() throws Exception {
        Long clienteIdInexistente = 9999L;
        
        mockMvc.perform(get(CLIENTE_API + "/" + clienteIdInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404 Not Found
    }
    
    // --- TESTAR GET /api/clientes (Listagem) ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarClientes_ComPaginacao_Retorna200() throws Exception {
        // Cria 3 clientes
        criarClienteNaBase("C1", "c1@teste.com");
        criarClienteNaBase("C2", "c2@teste.com");
        criarClienteNaBase("C3", "c3@teste.com");

        // Testa a listagem (assumindo que o endpoint retorna uma lista diretamente ou Page)
        mockMvc.perform(get(CLIENTE_API)
                .param("page", "0")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verifica se a página retornou 2 elementos (depende da implementação do Controller)
                .andExpect(jsonPath("$", hasSize(2)));
    }
    
    // --- TESTAR PUT /api/clientes/{id} (Atualização) ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAtualizarCliente_DadosValidos_Retorna200() throws Exception {
        Long clienteId = criarClienteNaBase("Antigo Nome", "antigo@teste.com");
        String payloadAtualizado = String.format("{\"nome\": \"Novo Nome Atualizado\", \"email\": \"antigo@teste.com\", \"role\": \"CLIENTE\"}");

        mockMvc.perform(put(CLIENTE_API + "/" + clienteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadAtualizado))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(clienteId))
                .andExpect(jsonPath("$.nome").value("Novo Nome Atualizado"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAtualizarCliente_Inexistente_Retorna404() throws Exception {
        String payloadAtualizado = "{\"nome\": \"Novo Nome Atualizado\", \"email\": \"antigo@teste.com\", \"role\": \"CLIENTE\"}";

        mockMvc.perform(put(CLIENTE_API + "/" + 9999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadAtualizado))
                .andExpect(status().isNotFound()); // 404 Not Found
    }
}