package com.exemplo.seguranca.util;

import com.exemplo.seguranca.modelo.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
             return (Usuario) authentication.getPrincipal();
        }
        return null;
    }

    public Long getCurrentUserId() {
        Usuario user = getCurrentUser();
        return (user != null) ? user.getId() : null;
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) { return false; }
        String fullRoleName = "ROLE_" + role.toUpperCase();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(fullRoleName));
    }
}