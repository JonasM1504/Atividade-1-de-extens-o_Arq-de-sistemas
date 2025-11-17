package com.exemplo.seguranca.repositorio;

import com.exemplo.seguranca.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para gerenciar operações de banco de dados da entidade Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Encontra um usuário pelo email
     * @param email o email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica se um usuário existe por email
     * @param email o email do usuário
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);
    
    /**
     * Encontra todos os usuários com uma determinada role
     * @param role a role/papel do usuário
     * @return lista de usuários com a role especificada
     */
    List<Usuario> findByRole(Usuario.Role role);
    
    /**
     * Encontra todos os usuários ativos
     * @return lista de usuários ativos
     */
    List<Usuario> findByAtivoTrue();
    
    /**
     * Encontra usuários por restaurante
     * @param restauranteId o ID do restaurante
     * @return lista de usuários associados ao restaurante
     */
    List<Usuario> findByRestauranteId(Long restauranteId);
    
    /**
     * Busca usuários ativos com uma determinada role
     * @param role a role/papel do usuário
     * @return lista de usuários ativos com a role especificada
     */
    @Query("SELECT u FROM Usuario u WHERE u.role = :role AND u.ativo = true")
    List<Usuario> findByRoleAndAtivoTrue(@Param("role") Usuario.Role role);
}