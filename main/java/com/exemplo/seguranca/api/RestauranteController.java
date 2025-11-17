// Arquivo: src/main/java/com/exemplo/seguranca/controlador/RestauranteController.java

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    // POST /api/restaurantes
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createRestaurante() { return "Restaurante criado (ADMIN)"; }

    // PUT /api/restaurantes/{id}
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.isOwner(#id))")
    @PutMapping("/{id}")
    public String updateRestaurante(@PathVariable Long id) { return "Restaurante " + id + " atualizado"; }

    // DELETE /api/restaurantes/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteRestaurante(@PathVariable Long id) { return "Restaurante " + id + " deletado (ADMIN)"; }
    
    // GET /api/restaurantes (público - não precisa de @PreAuthorize)
    @GetMapping
    public String getRestaurantes() { return "Lista de Restaurantes (PÚBLICO)"; }
}
