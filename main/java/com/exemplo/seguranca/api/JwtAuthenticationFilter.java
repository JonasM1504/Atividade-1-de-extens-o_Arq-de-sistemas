package com.exemplo.seguranca.filtro;

import com.exemplo.seguranca.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT para validar tokens em cada requisição
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;

            // 1. Verificar e Extrair token do header Authorization (Bearer token)
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);
            
            try {
                userEmail = jwtUtil.extractUsername(jwt);
            } catch (JwtException e) {
                log.warn("Token JWT inválido: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token JWT inválido ou expirado.\"}");
                return;
            } catch (Exception e) {
                log.error("Erro ao processar token JWT", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Erro ao processar autenticação.\"}");
                return;
            }

            // 2. Validar token e carregar usuário no SecurityContext
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        // Carrega o usuário autenticado no SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Usuário {} autenticado com sucesso", userEmail);
                    } else {
                        log.warn("Token inválido ou expirado para usuário: {}", userEmail);
                    }
                } catch (Exception e) {
                    log.error("Erro ao carregar usuário: {}", userEmail, e);
                }
            }
            
            // 3. Permitir passagem para endpoints públicos (e os agora autenticados)
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Erro no filtro JWT", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Erro interno do servidor.\"}");
        }
    }
}