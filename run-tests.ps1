<#
run-tests.ps1
Script helper para executar testes do projeto no Windows PowerShell.

Funcionalidade:
- Detecta `mvnw.cmd` (Maven Wrapper) e executa `mvnw.cmd test` se existir.
- Se não houver wrapper, detecta `mvn` no PATH e executa `mvn test`.
- Se nenhum estiver disponível, informa como instalar Maven ou adicionar wrapper.

Uso:
No PowerShell (na raiz do projeto):
PS> .\run-tests.ps1

Se precisar definir `JWT_SECRET` somente para a execução atual:
PS> $env:JWT_SECRET = 'SUA_CHAVE_BASE64_AQUI'
PS> .\run-tests.ps1
#>

param()

Write-Host "[run-tests] Iniciando verificação de ambiente..." -ForegroundColor Cyan

# Caminho para o wrapper no Windows
$wrapper = Join-Path -Path (Get-Location) -ChildPath 'mvnw.cmd'

if (Test-Path $wrapper) {
    Write-Host "[run-tests] Encontrado Maven Wrapper 'mvnw.cmd'. Executando testes via wrapper..." -ForegroundColor Green
    & "$wrapper" test
    exit $LASTEXITCODE
}

# Verificar se 'mvn' está disponível
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvnCmd) {
    Write-Host "[run-tests] Encontrado 'mvn' no PATH. Executando: mvn test" -ForegroundColor Green
    mvn test
    exit $LASTEXITCODE
}

# Nenhuma ferramenta encontrada
Write-Host "[run-tests] Nem 'mvnw.cmd' nem 'mvn' foram encontrados." -ForegroundColor Yellow
Write-Host "Siga uma das opções abaixo para executar os testes:" -ForegroundColor Yellow

Write-Host "1) Usar Maven instalado localmente (recomendado):" -ForegroundColor White
Write-Host "   - Instale JDK 11+ e Apache Maven." -ForegroundColor White
Write-Host "   - Verifique com: mvn -v" -ForegroundColor White

Write-Host "2) Adicionar Maven Wrapper ao projeto (opção conveniente):" -ForegroundColor White
Write-Host "   - Em um ambiente com Maven instalado, rode na raiz do projeto:" -ForegroundColor White
Write-Host "     mvn -N io.takari:maven:wrapper" -ForegroundColor Gray
Write-Host "   - Isso criará 'mvnw' e 'mvnw.cmd' no projeto, permitindo execução portátil." -ForegroundColor White

Write-Host "3) Rodar em outro ambiente: você pode executar testes em CI (GitHub Actions, etc.)." -ForegroundColor White

Write-Host "Exemplo (definir JWT secret temporariamente e executar):" -ForegroundColor Cyan
Write-Host "    $env:JWT_SECRET = 'SUA_CHAVE_BASE64_AQUI'" -ForegroundColor Gray
Write-Host "    .\\run-tests.ps1" -ForegroundColor Gray

exit 1
