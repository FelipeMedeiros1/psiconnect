# PsiConnect

API Spring Boot para gerenciamento de atendimentos, pacientes, psicologos, sessoes e financeiro.

## Tecnologias

- Java 17
- Spring Boot 3.3.4
- Maven Wrapper
- Spring Web
- Spring Data JPA
- Bean Validation
- Flyway
- H2 para desenvolvimento local
- Swagger/OpenAPI

## Pre-requisitos

Instale na maquina:

- Git
- JDK 17 ou superior

Nao e necessario instalar Maven manualmente. O projeto possui Maven Wrapper:

- Windows: `mvnw.cmd`
- Linux/macOS: `./mvnw`

## Como baixar o projeto

```bash
git clone https://github.com/FelipeMedeiros1/psiconnect.git
cd psiconnect
```

## Como rodar em desenvolvimento

O perfil padrao e `dev`, configurado em `src/main/resources/application.yml`.

No Windows:

```bash
mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

A aplicacao sobe em:

```text
http://localhost:8080
```

## Perfil dev

O perfil `dev` usa banco H2 em memoria:

```text
JDBC URL: jdbc:h2:mem:psiconnect
Usuario: sa
Senha: em branco
```

Console do H2:

```text
http://localhost:8080/h2-console
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Selecionar outro perfil

O perfil ativo pode ser definido pela variavel de ambiente `SPRING_PROFILES_ACTIVE`.

Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Perfis existentes:

- `dev`: H2 em memoria, recomendado para rodar localmente.
- `homolog`: configurado para PostgreSQL em `localhost:5432/psiconnect_homolog`.
- `prod`: configurado para PostgreSQL em `prod-db-host:5432/psiconnect`.

Observacao: os perfis `homolog` e `prod` usam `org.postgresql.Driver` nas configuracoes. Para rodar esses perfis, confirme se o driver PostgreSQL esta no `pom.xml` ou ajuste o banco/driver conforme o ambiente.

## Configuracoes de ambiente

Arquivos `.env*` ficam ignorados pelo Git. Em outra maquina, crie os arquivos locais que forem necessarios ou configure as variaveis diretamente no sistema/IDE.

Variavel principal:

```text
SPRING_PROFILES_ACTIVE=dev
```

Para desenvolvimento local, nenhuma variavel extra e obrigatoria porque o perfil `dev` ja usa H2 em memoria.

## Rodar os testes

Windows:

```bash
mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

## Gerar o build

Windows:

```bash
mvnw.cmd clean package
```

Linux/macOS:

```bash
./mvnw clean package
```

O arquivo `.jar` sera gerado em `target/`.

## Rodar o `.jar`

Depois do build:

```bash
java -jar target/PsiConnect-0.0.1-SNAPSHOT.jar
```

Para escolher o perfil:

```bash
java -jar target/PsiConnect-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## Dicas para IDE

Ao importar o projeto:

- Use JDK 17 ou superior.
- Importe como projeto Maven.
- Habilite annotation processing para o Lombok, se a IDE solicitar.
- Rode a classe principal `br.com.psiconnect.Application`.
- Se quiser definir perfil pela IDE, adicione a variavel de ambiente `SPRING_PROFILES_ACTIVE=dev`.

## Problemas comuns

Se o comando `mvn` nao existir, use o Maven Wrapper:

```bash
mvnw.cmd test
```

Se a aplicacao nao subir em `homolog` ou `prod`, verifique:

- se o banco PostgreSQL esta rodando;
- se URL, usuario e senha estao corretos;
- se o driver PostgreSQL esta configurado no `pom.xml`;
- se o perfil ativo esta correto.
