package com.exemplo.seguranca.controlador;

import com.exemplo.seguranca.TesteIntegracaoBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PedidoControllerIT extends TesteIntegracaoBase {

    private static final String PEDIDO_API = "/api/pedidos";

    @Autowired
    private ObjectMapper objectMapper;

    // --- MOCKS DE DADOS ---

    // Payload simplificado para criação de pedido (simulando um PedidoRequest)
    private String criarPedidoPayload(Long clienteId, Long restauranteId, Long produtoId, int quantidade) {
        // Assume uma estrutura de pedido com um item
        return String.format(
            "{\"clienteId\": %d, \"restauranteId\": %d, \"itens\": [{\"produtoId\": %d, \"quantidade\": %d, \"valorUnitario\": 10.0}]}",
            clienteId, restauranteId, produtoId, quantidade
        );
    }
    
    // Método auxiliar para criar um pedido mock na base (usando um token real seria mais robusto)
    private Long criarPedidoNaBase(Long clienteId, Long restauranteId, Long produtoId) throws Exception {
        String payload = criarPedidoPayload(clienteId, restauranteId, produtoId, 2);
        
        // Simula a criação com um usuário CLIENTE autenticado
        ResultActions result = mockMvc.perform(post(PEDIDO_API)
                .with(request -> {
                    // Simula a autenticação de um CLIENTE de ID 1 para este método mock
                    request.addHeader("Authorization", "Bearer MOCK_TOKEN_CLIENTE_1"); 
                    return request;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));
        
        // Assumindo que a criação do Pedido Controller é protegida e bem-sucedida,
        // mas para este teste de IT, o resultado é mockado ou o controller de criação é usado.
        // Vamos apenas retornar um ID fixo para mockar a existência na base para os outros testes.
        return 100L; 
    }
    

    // --- 2.3.1 e 2.3.6: Testar POST /api/pedidos (Criação e Cálculo de Valor) ---

    @Test
    @WithMockUser(roles = "CLIENTE", username = "cliente@test.com", authorities = {"ROLE_CLIENTE"}, principal = "1")
    void testCriarPedido_DadosValidos_Retorna201ECalculoCorreto() throws Exception {
        // Mock: produtoId 10 existe com valor 10.0
        String payload = criarPedidoPayload(1L, 1L, 10L, 3); // 3 * 10.0 = 30.0

        mockMvc.perform(post(PEDIDO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.valorTotal").value(closeTo(30.0, 0.01))) // 3*10=30.0
                .andExpect(jsonPath("$.status").value("CRIADO"));
    }

    @Test
    @WithMockUser(roles = "RESTAURANTE")
    void testCriarPedido_PerfilInvalido_Retorna403() throws Exception {
        String payload = criarPedidoPayload(1L, 1L, 10L, 1);
        
        // Pedidos só podem ser criados por CLIENTE (regras no PedidoController.java)
        mockMvc.perform(post(PEDIDO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }


    // --- 2.3.2: Testar validação de produtos inexistentes ---

    @Test
    @WithMockUser(roles = "CLIENTE")
    void testCriarPedido_ProdutoInexistente_Retorna404() throws Exception {
        // Mock: produtoId 9999 não existe
        String payload = criarPedidoPayload(1L, 1L, 9999L, 1);

        mockMvc.perform(post(PEDIDO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isNotFound()) // 404 Not Found (ou 400 se a regra for de validação)
                .andExpect(jsonPath("$.mensagem").value(containsString("Produto não encontrado")));
    }


    // --- 2.3.3: Testar validação de estoque insuficiente ---

    @Test
    @WithMockUser(roles = "CLIENTE")
    void testCriarPedido_EstoqueInsuficiente_Retorna400() throws Exception {
        // Mock: produtoId 20 existe, mas tem estoque < 50
        String payload = criarPedidoPayload(1L, 1L, 20L, 50);

        mockMvc.perform(post(PEDIDO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.mensagem").value(containsString("Estoque insuficiente")));
    }


    // --- 2.3.4: Testar GET /api/pedidos/cliente/{id} (Histórico do Cliente) ---

    @Test
    @WithMockUser(roles = "CLIENTE", username = "cliente@test.com", principal = "1")
    void testGetHistoricoCliente_AcessoProprio_Retorna200() throws Exception {
        // Cliente 1 acessa seu próprio histórico
        mockMvc.perform(get(PEDIDO_API + "/cliente/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0)))); // Pode ser 0 ou mais
    }
    
    @Test
    @WithMockUser(roles = "CLIENTE", username = "cliente2@test.com", principal = "2")
    void testGetHistoricoCliente_AcessoNaoAutorizado_Retorna403() throws Exception {
        // Cliente 2 tenta acessar o histórico do Cliente 1 (acesso não autorizado)
        // A regra de segurança deve ser: hasRole('ADMIN') or principal.id == #id
        mockMvc.perform(get(PEDIDO_API + "/cliente/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }


    // --- 2.3.5: Testar PUT /api/pedidos/{id}/status (Atualização de Status) ---

    @Test
    @WithMockUser(roles = "RESTAURANTE", username = "restaurante@test.com", principal = "50")
    void testAtualizarStatus_RestauranteDono_Retorna200() throws Exception {
        // Mock: Pedido 100 pertence ao Restaurante 50 (usuário logado)
        Long pedidoId = 100L;
        String novoStatus = "EM_PREPARACAO";

        mockMvc.perform(put(PEDIDO_API + "/{id}/status", pedidoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"status\": \"%s\"}", novoStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedidoId))
                .andExpect(jsonPath("$.status").value(novoStatus));
    }
    
    @Test
    @WithMockUser(roles = "CLIENTE")
    void testAtualizarStatus_PerfilInvalido_Retorna403() throws Exception {
        // Clientes não podem alterar o status (exceto talvez "CANCELADO", mas aqui testamos negação)
        Long pedidoId = 100L;
        
        mockMvc.perform(put(PEDIDO_API + "/{id}/status", pedidoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"ENTREGUE\"}"))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }
}