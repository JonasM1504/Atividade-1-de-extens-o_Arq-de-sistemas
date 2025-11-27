package com.exemplo.seguranca.controlador;

import com.exemplo.seguranca.TesteIntegracaoBase;
// ... imports adicionais ...

public class RestauranteControllerIT extends TesteIntegracaoBase {

    // Teste 1: POST /restaurantes deve ser 403 Forbidden para CLIENTE.
    // Teste 2: POST /restaurantes deve ser 200 OK para ADMIN.
    // Teste 3: PUT /restaurantes/{id} (se for dono) deve ser 200 OK para RESTAURANTE.
    // Teste 4: PUT /restaurantes/{id} (se NÃO for dono) deve ser 403 Forbidden para RESTAURANTE.
}