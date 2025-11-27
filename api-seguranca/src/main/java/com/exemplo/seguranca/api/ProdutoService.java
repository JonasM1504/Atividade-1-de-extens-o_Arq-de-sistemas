package com.exemplo.seguranca.servico;

import com.exemplo.seguranca.util.SecurityUtils;
import org.springframework.stereotype.Service;

@Service("produtoService") // Nome do Bean para o @PreAuthorize
public class ProdutoService {

    private final SecurityUtils securityUtils;

    public ProdutoService(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public boolean isOwner(Long produtoId) {
        Long userRestauranteId = securityUtils.getCurrentUser() != null ? securityUtils.getCurrentUser().getRestauranteId() : null;
        if (userRestauranteId == null) {
            return false;
        }
        
        // Lógica Mock: Assumindo que o produto com ID 99 pertence ao restaurante com ID 50 (do usuário mock)
        // Em um sistema real, buscaria o produto e compararia seu restauranteId.
        if (produtoId.equals(99L) && userRestauranteId.equals(50L)) { 
            return true; 
        }
        return false;
    }
}