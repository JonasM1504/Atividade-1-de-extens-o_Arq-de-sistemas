package com.exemplo.seguranca.controlador;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @GetMapping // Público, regrada no SecurityConfig
    public String getProdutos() { return "Lista de Produtos (PÚBLICO)"; }

    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @PostMapping
    public String createProduto() { return "Produto criado (RESTAURANTE/ADMIN)"; }

    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @PutMapping("/{id}")
    public String updateProduto(@PathVariable Long id) { return "Produto " + id + " atualizado"; }

    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @DeleteMapping("/{id}")
    public String deleteProduto(@PathVariable Long id) { return "Produto " + id + " deletado"; }
}