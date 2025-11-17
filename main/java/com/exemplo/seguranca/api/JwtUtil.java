// Arquivo: src/main/java/com/exemplo/seguranca/util/JwtUtil.java

package com.exemplo.seguranca.util;

import com.exemplo.seguranca.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    // 2.1 Configurar chave secreta segura (mínimo 256 bits)
    // Armazenada no application.properties
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Tempo de expiração (24 horas em milissegundos)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; 

    // Obtém a chave de assinatura com base na chave secreta
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- MÉTODOS DE EXTRAÇÃO ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // --- MÉTODOS DE VALIDAÇÃO E GERAÇÃO ---

    public Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        // Incluir claims customizados: userId, role, restauranteId
        Usuario usuario = (Usuario) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        claims.put("role", usuario.getRole().name());
        if (usuario.getRestauranteId() != null) {
            claims.put("restauranteId", usuario.getRestauranteId());
        }

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject) // O email/username
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
}