// Arquivo: src/main/java/com/exemplo/seguranca/servico/RestauranteService.java
package com.exemplo.seguranca.servico;

import com.exemplo.seguranca.util.SecurityUtils;
import org.springframework.stereotype.Service;

@Service("restauranteService") // Nome do Bean deve ser especificado para o SpEL
public class RestauranteService {

    private final SecurityUtils securityUtils;
    // private final RestauranteRepository restauranteRepository; // Seria injetado

    public RestauranteService(SecurityUtils securityUtils /*, RestauranteRepository repo */) {
        this.securityUtils = securityUtils;
        // this.restauranteRepository = repo;
    }

    /** Verifica se o usuário logado é o proprietário do restaurante. */
    public boolean isOwner(Long restauranteId) {
        // 1. Obtém o ID do usuário logado
        Long loggedUserId = securityUtils.getCurrentUserId();
        if (loggedUserId == null) {
            return false;
        }
        
        // 2. Obtém o usuário logado (assumindo que o restauranteId está na entidade Usuario)
        Long userRestauranteId = securityUtils.getCurrentUser().getRestauranteId();
        
        // Em um cenário real, você faria:
        // Restaurante restaurante = restauranteRepository.findById(restauranteId).orElse(null);
        // return restaurante != null && restaurante.getOwnerId().equals(loggedUserId);
        
        // Implementação simplificada baseada na entidade Usuario:
        if (userRestauranteId != null && userRestauranteId.equals(restauranteId)) {
             return true;
        }
        return false;
    }
}