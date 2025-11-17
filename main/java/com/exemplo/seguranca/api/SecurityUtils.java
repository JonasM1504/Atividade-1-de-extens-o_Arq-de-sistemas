// Arquivo: src/main/java/com/exemplo/seguranca/util/SecurityUtils.java

package com.exemplo.seguranca.util;

import com.exemplo.seguranca.modelo.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /** Retorna o objeto Usuario logado, ou null se não estiver autenticado. */
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // O principal deve ser nosso objeto Usuario que implementa UserDetails
            if (authentication.getPrincipal() instanceof Usuario) {
                 return (Usuario) authentication.getPrincipal();
            }
            
            // Em caso de testes ou se o principal for apenas o UserDetails padrão, pode ser necessário 
            // buscar no banco, mas aqui assumimos que o principal é o Usuario.
        }
        return null;
    }

    /** Retorna o ID do usuário logado, ou null se não estiver autenticado. */
    public Long getCurrentUserId() {
        Usuario user = getCurrentUser();
        return (user != null) ? user.getId() : null;
    }

    /** Verifica se o usuário logado tem uma role específica. */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        // As roles são armazenadas no formato "ROLE_NOME"
        String fullRoleName = "ROLE_" + role.toUpperCase();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(fullRoleName));
    }
}