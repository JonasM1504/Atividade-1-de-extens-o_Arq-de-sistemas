package com.exemplo.seguranca.servico;

import com.exemplo.seguranca.util.SecurityUtils;
import org.springframework.stereotype.Service;

@Service("restauranteService") // Nome do Bean para o @PreAuthorize
public class RestauranteService {

    private final SecurityUtils securityUtils;

    public RestauranteService(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public boolean isOwner(Long restauranteId) {
        Long userRestauranteId = securityUtils.getCurrentUser() != null ? securityUtils.getCurrentUser().getRestauranteId() : null;
        
        // Retorna true se o ID do restaurante do usuário logado for igual ao ID passado
        return userRestauranteId != null && userRestauranteId.equals(restauranteId);
    }
}