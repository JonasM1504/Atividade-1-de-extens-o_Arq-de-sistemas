package com.exemplo.seguranca.api;

import com.exemplo.seguranca.modelo.Usuario;
import com.exemplo.seguranca.repositorio.UsuarioRepository;
import com.exemplo.seguranca.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

/**
 * Controller para endpoints de autenticação
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                         UserDetailsService userDetailsService,
                         UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint de login - autentica usuário e retorna JWT
     * @param loginRequest email e senha do usuário
     * @return Token JWT e informações do usuário autenticado
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 1. Autenticar com email e senha
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getSenha()
                )
            );

            // 2. Carregar usuário e gerar token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);
            
            // 3. Extrair informações adicionais
            Usuario usuario = (Usuario) userDetails;
            
            log.info("Usuário {} autenticado com sucesso", loginRequest.getEmail());
            
            // 4. Retornar resposta com token
            return ResponseEntity.ok(new AuthResponse(
                jwt,
                "Login realizado com sucesso.",
                usuario.getId(),
                usuario.getRole().name()
            ));
            
        } catch (BadCredentialsException e) {
            log.warn("Falha na autenticação: credenciais inválidas para {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(null, "Email ou senha inválidos.", null, null));
        } catch (Exception e) {
            log.error("Erro ao autenticar usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(null, "Erro ao processar autenticação.", null, null));
        }
    }

    /**
     * Endpoint GET /api/auth/me - retorna dados do usuário autenticado
     * @return Dados do usuário logado
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getLoggedUser() {
        // Obtém o usuário autenticado do SecurityContext (carregado pelo JwtAuthenticationFilter)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // Retornar dados do usuário logado
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        return ResponseEntity.ok(UserResponse.fromEntity(usuario));
    }
}