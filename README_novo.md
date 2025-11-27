# API RESTful de Segurança com JWT

Este projeto implementa uma API RESTful completa utilizando Spring Boot com Spring Security para gerenciar autenticação (Login/Registro) com Tokens JWT e autorização baseada em roles (RBAC).

## Objetivo

Validar o comportamento completo dos controladores sob diferentes contextos de segurança, garantindo que apenas usuários com as permissões corretas possam acessar os recursos.

## Pré-requisitos

- Java JDK 17 ou superior (CI testa em 11, 17 e 21)
- Maven 3.x ou Maven Wrapper
- Ferramenta para testar endpoints (Postman, Insomnia ou cURL)

## Como Compilar e Executar

### Compilar o projeto

```bash
mvn clean install
```

### Executar a aplicação web

```powershell
mvn spring-boot:run
```

Ou, se tiver Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

## Como Rodar os Testes

### Opção 1 - Com Maven instalado

```powershell
mvn test
```

### Opção 2 - Com Maven Wrapper

```powershell
.\mvnw.cmd test
```

### Opção 3 - Script helper

```powershell
$env:JWT_SECRET = 'SUA_CHAVE_BASE64_DE_32_BYTES'
.\run-tests.ps1
```

## Variáveis de Ambiente

- `JWT_SECRET` — Chave BASE64 (mínimo 32 bytes / 256 bits). Deve ser configurada em produção.

Exemplo (PowerShell):

```powershell
$env:JWT_SECRET = 'U3VhU2VuaGFTZWN1cmFDb21FeGFtcGxv'
```

## Observações

- O projeto usa Jakarta Persistence (pacotes `jakarta.*`).
- Para executar testes que dependem do JWT, defina `JWT_SECRET` antes de rodar os testes.
- O banco de dados padrão é H2 em memória para desenvolvimento.

## Console (modo não-HTTP)

Para executar operações básicas localmente (listar usuários, criar usuário, gerar JWT) sem iniciar o servidor web, veja `CONSOLE.md` para instruções detalhadas.

## CI/CD

O workflow de CI publica o JAR gerado como artefato no run das ações do GitHub. Você pode baixar o artefato pela página da execução do workflow no GitHub.
