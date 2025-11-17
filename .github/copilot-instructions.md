# Copilot Instructions - API Segurança

## Arquitetura Geral

Esta é uma API Spring Boot com autenticação baseada em **JWT (JSON Web Tokens)**. A arquitetura segue um padrão de segurança stateless com múltiplas roles de usuário.

### Componentes Principais

1. **`Usuario`** - Entidade JPA que implementa `UserDetails` do Spring Security
   - Roles: CLIENTE, RESTAURANTE, ADMIN, ENTREGADOR
   - Campos críticos: `email` (único), `senha`, `role`, `restauranteId`, `ativo`
   - Sempre implementa `getAuthorities()` retornando `"ROLE_" + role.name()`

2. **`JwtUtil`** - Utilitário de geração e validação de tokens JWT
   - Injeta `${jwt.secret}` (BASE64, mínimo 256 bits) do `application.properties`
   - Adiciona claims customizados: `userId`, `role`, `restauranteId`
   - Expiração: 24 horas (`EXPIRATION_TIME = 1000 * 60 * 60 * 24`)
   - **Método-chave**: `generateToken(UserDetails)` - inclui claims do `Usuario`

3. **`JwtAuthenticationFilter`** - Filtro que valida tokens em cada requisição
   - Extrai token do header `Authorization: Bearer <token>`
   - Carrega usuário no `SecurityContext` se token válido
   - Retorna **401 Unauthorized** para tokens inválidos/expirados
   - Integrado na `SecurityFilterChain` **antes** de `UsernamePasswordAuthenticationFilter`

4. **`SecurityConfig`** - Configuração de segurança da aplicação
   - **Crítico**: `sessionCreationPolicy(SessionCreationPolicy.STATELESS)` - obrigatório com JWT
   - Endpoints públicos: `/api/auth/**` (POST), `/api/restaurantes` (GET), `/api/produtos` (GET)
   - Todos os demais endpoints requerem autenticação
   - CORS habilitado via `corsConfigurationSource()`

5. **`AuthController`** - Endpoint de autenticação
   - **`/api/auth/login`** (POST): Autentica com email/senha, retorna JWT
   - Injetar `JwtUtil` e `UserDetailsService` para gerar token após autenticação bem-sucedida

6. **`UsuarioRepository`** - Acesso a dados de usuários
   - Métodos customizados: `findByEmail()`, `existsByEmail()`, `findByRole()`, `findByAtivoTrue()`
   - Query customizada: `findByRoleAndAtivoTrue()` - usuarios ativos com role específica

## Fluxo de Autenticação

```
Cliente → /api/auth/login (email+senha) → AuthController
    ↓
AuthenticationManager valida credenciais
    ↓
JwtUtil.generateToken() cria JWT com claims (userId, role, restauranteId)
    ↓
Client recebe token, envia em próximas requisições via "Authorization: Bearer <token>"
    ↓
JwtAuthenticationFilter intercepta, extrai token, carrega usuário em SecurityContext
    ↓
Endpoint protegido pode acessar usuário autenticado via @AuthenticationPrincipal ou SecurityContext
```

## Convenções e Padrões

### Estrutura de Packages
- `com.exemplo.seguranca.modelo` - Entidades JPA (`Usuario`)
- `com.exemplo.seguranca.util` - Utilitários (`JwtUtil`)
- `com.exemplo.seguranca.filtro` - Filtros Spring (`JwtAuthenticationFilter`)
- `com.exemplo.seguranca.api` - Controllers e Config
- `com.exemplo.seguranca.repositorio` - Repositories

### Claims JWT Padrão
Ao gerar tokens, incluir SEMPRE:
```java
claims.put("userId", usuario.getId());
claims.put("role", usuario.getRole().name());
if (usuario.getRestauranteId() != null) {
    claims.put("restauranteId", usuario.getRestauranteId());
}
```

### Validação de Token
```java
// Usar sempre assim:
if (jwtUtil.validateToken(token, userDetails)) {
    // Token válido: não expirado e username coincide
}
```

### Endpoint de Teste
- Endpoints GET `/api/restaurantes` e `/api/produtos` são públicos (`.permitAll()`)
- Para testar protegidos: usar `@WithMockUser(roles = "ADMIN")` em testes

## Testes

- **Framework**: JUnit 5 + Spring Boot Test + MockMvc
- **Arquivo de teste principal**: `JwtUtilTest.java`
  - Usa `ReflectionTestUtils.setField()` para injetar `SECRET_KEY` em testes
  - Cria mock `Usuario` implementando `UserDetails`
  - Testa geração, extração de claims e validação de token
- **Testes de integração**: `ApiSegurancaApplicationTests.java`
  - Usa `@SpringBootTest` e `@AutoConfigureMockMvc`
  - Testa endpoints públicos (200) vs protegidos sem auth (401) vs com auth (200)

## Configuração

- **Banco de dados**: H2 em memória (development)
- **JWT Secret**: Deve ter **mínimo 32 caracteres BASE64** (256 bits)
- **Spring Boot versão**: Usa Jakarta Persistence (não `javax.persistence`)
- **Segurança CSRF**: Desabilitado (`.csrf(AbstractHttpConfigurer::disable)`)

## Desenvolvimento

1. **Adicionar novo endpoint protegido**: Não precisa de configuração extra, apenas `@RequestMapping` - `SecurityConfig` bloqueia tudo por padrão
2. **Adicionar novo endpoint público**: Adicionar em `SecurityConfig.authorizeHttpRequests()` com `.permitAll()`
3. **Gerar token manualmente**: `jwtUtil.generateToken(usuario)` - retorna String JWT válida
4. **Extrair informações do token**: `jwtUtil.extractClaim(token, claims -> claims.get("key", Type.class))`
5. **Teste de requisição com JWT**: `Authorization: Bearer <token>` no header
