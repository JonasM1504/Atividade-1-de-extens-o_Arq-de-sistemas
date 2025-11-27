package com.exemplo.seguranca.controlador;

import com.exemplo.seguranca.dto.*;
import com.exemplo.seguranca.modelo.Usuario;
import com.exemplo.seguranca.repositorio.UsuarioRepository;
import com.exemplo.seguranca.util.JwtUtil;
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
import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(
        AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
        UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já está em uso.");
        }
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(request.nome());
        novoUsuario.setEmail(request.email());
        novoUsuario.setRole(request.role());
        novoUsuario.setRestauranteId(request.restauranteId());
        novoUsuario.setSenha(passwordEncoder.encode(request.senha()));
        Usuario salvo = usuarioRepository.save(novoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromEntity(salvo));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
            final String jwt = jwtUtil.generateToken(userDetails);
            Date expirationDate = jwtUtil.extractClaim(jwt, claims -> claims.getExpiration());
            Usuario usuarioLogado = (Usuario) userDetails;
            
            return ResponseEntity.ok(
                new LoginResponse(jwt, UserResponse.fromEntity(usuarioLogado), expirationDate.toInstant())
            );

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas: Email ou Senha incorretos.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        return ResponseEntity.ok(UserResponse.fromEntity(usuario));
    }
}