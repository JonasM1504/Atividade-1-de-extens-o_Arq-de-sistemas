// Arquivo: src/main/java/com/exemplo/seguranca/controlador/ProdutoController.java

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    // GET /api/produtos (público - não precisa de @PreAuthorize)
    @GetMapping
    public String getProdutos() { return "Lista de Produtos (PÚBLICO)"; }

    // POST /api/produtos
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @PostMapping
    public String createProduto() { return "Produto criado (RESTAURANTE/ADMIN)"; }

    // PUT /api/produtos/{id}
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @PutMapping("/{id}")
    public String updateProduto(@PathVariable Long id) { return "Produto " + id + " atualizado"; }

    // DELETE /api/produtos/{id}
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @DeleteMapping("/{id}")
    public String deleteProduto(@PathVariable Long id) { return "Produto " + id + " deletado"; }
}