package com.exemplo.seguranca.controlador;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public String createPedido() { return "Pedido criado (CLIENTE)"; }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String getAllPedidos() { return "Lista de Todos Pedidos (ADMIN)"; }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/meus")
    public String getMeusPedidos() { return "Meus Pedidos (CLIENTE)"; }

    @PreAuthorize("hasRole('RESTAURANTE')")
    @GetMapping("/restaurante")
    public String getPedidosRestaurante() { return "Pedidos do Meu Restaurante (RESTAURANTE)"; }
}