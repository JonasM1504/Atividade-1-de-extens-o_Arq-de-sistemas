package com.exemplo.seguranca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest // Carrega o contexto completo da aplicação
@AutoConfigureMockMvc // Configura o MockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Garante isolamento
@ActiveProfiles("test") // Ativa o application-test.properties
public abstract class TesteIntegracaoBase {

    @Autowired
    protected MockMvc mockMvc; // Usado para simular requisições HTTP

    @TestConfiguration 
    static class TestDataLoader {
        // Opcional: Aqui você pode criar um método @PostConstruct para inserir dados iniciais 
        // (como um usuário ADMIN ou CLIENTE) diretamente no banco H2 para uso nos testes.
    }
}