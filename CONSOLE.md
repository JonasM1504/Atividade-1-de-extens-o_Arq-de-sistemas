# Console - Instruções (modo não-HTTP)

Este arquivo descreve como usar a pequena interface de console adicionada ao projeto.

Classe principal: `com.exemplo.seguranca.console.ConsoleApplication`

O console inicializa o contexto Spring sem servidor web e permite operações simples:
- Listar usuários
- Criar usuário
- Login (gera JWT)

Execução (PowerShell):

1) Via Maven exec:

```powershell
mvn org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass="com.exemplo.seguranca.console.ConsoleApplication"
```

2) Via JAR (após empacotar):

```powershell
mvn -DskipTests package
java -jar target\seu-artifact-id-<versao>.jar --spring.main.web-application-type=none
```

Dica: defina `JWT_SECRET` na sessão se desejar controlar o secret usado pelo `JwtUtil`:

```powershell
$env:JWT_SECRET = 'SUA_CHAVE_BASE64_DE_32_BYTES'
```

Observações:
- O banco H2 em memória é usado por padrão; os dados do console não persistem entre execuções.
- O console é útil para demonstrações locais e testes rápidos.
