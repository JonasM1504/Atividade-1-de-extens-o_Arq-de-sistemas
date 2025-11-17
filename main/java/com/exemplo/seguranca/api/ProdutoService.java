// Arquivo: src/main/java/com/exemplo/seguranca/servico/ProdutoService.java
package com.exemplo.seguranca.servico;

import com.exemplo.seguranca.util.SecurityUtils;
import org.springframework.stereotype.Service;

@Service("produtoService") // Nome do Bean deve ser especificado para o SpEL
public class ProdutoService {

    private final SecurityUtils securityUtils;
    // private final ProdutoRepository produtoRepository; // Seria injetado

    public ProdutoService(SecurityUtils securityUtils /*, ProdutoRepository repo */) {
        this.securityUtils = securityUtils;
        // this.produtoRepository = repo;
    }

    /** Verifica se o produto pertence ao restaurante do usuário logado. */
    public boolean isOwner(Long produtoId) {
        // 1. Obtém o ID do Restaurante associado ao usuário logado
        Long userRestauranteId = securityUtils.getCurrentUser() != null ? securityUtils.getCurrentUser().getRestauranteId() : null;
        if (userRestauranteId == null) {
            return false;
        }
        
        // 2. Lógica de Negócio (Busca real seria aqui):
        // Produto produto = produtoRepository.findById(produtoId).orElse(null);
        // return produto != null && produto.getRestauranteId().equals(userRestauranteId);
        
        // Implementação Mock para o teste de autorização:
        // Assumimos que o produto com ID 99 pertence ao restaurante 50 (do usuário mock)
        if (produtoId.equals(99L) && userRestauranteId.equals(50L)) { 
            return true; 
        }
        return false;
    }
}