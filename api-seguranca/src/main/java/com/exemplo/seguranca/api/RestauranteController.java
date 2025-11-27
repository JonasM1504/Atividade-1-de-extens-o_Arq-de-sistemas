package com.exemplo.seguranca.controlador;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    @GetMapping // Público, regrada no SecurityConfig
    public String getRestaurantes() { return "Lista de Restaurantes (PÚBLICO)"; }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createRestaurante() { return "Restaurante criado (ADMIN)"; }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.isOwner(#id))")
    @PutMapping("/{id}")
    public String updateRestaurante(@PathVariable Long id) { return "Restaurante " + id + " atualizado"; }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteRestaurante(@PathVariable Long id) { return "Restaurante " + id + " deletado (ADMIN)"; }
}