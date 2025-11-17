// Arquivo: src/main/java/com/exemplo/seguranca/controlador/PedidoController.java

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    // POST /api/pedidos
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public String createPedido() { return "Pedido criado (CLIENTE)"; }

    // GET /api/pedidos
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String getAllPedidos() { return "Lista de Todos Pedidos (ADMIN)"; }

    // GET /api/pedidos/meus
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/meus")
    public String getMeusPedidos() { return "Meus Pedidos (CLIENTE)"; }

    // GET /api/pedidos/restaurante
    @PreAuthorize("hasRole('RESTAURANTE')")
    @GetMapping("/restaurante")
    public String getPedidosRestaurante() { return "Pedidos do Meu Restaurante (RESTAURANTE)"; }
}