# PsiConnect

Aplicação Spring Boot / Java 17 organizada pelo contexto de negócio **Consultório**.

## Arquitetura DDD

```text
br.com.psiconnect.consultorio
├── domain
│   ├── paciente       # Paciente e Responsavel
│   ├── psicologo      # Psicologo e Especialidade
│   ├── consulta       # Sessao
│   ├── contato        # Objeto de valor Contato
│   ├── endereco       # Objeto de valor Endereco
│   └── exception      # Exceções de negócio
├── application
│   ├── paciente       # Casos de uso e DTOs
│   ├── psicologo      # Casos de uso e DTOs
│   ├── consulta       # Agendamento, relatórios e validações entre entidades
│   ├── contato, endereco # DTOs compartilhados
│   ├── mapper         # Conversão dos DTOs em valores de domínio
│   └── port           # Contratos de acesso à persistência
└── infrastructure
    ├── persistence    # Implementações Spring Data JPA e consultas JPQL
    └── web            # Controllers e tradução de erros para HTTP
```

O domínio não importa aplicação, infraestrutura, Spring ou DTOs. Paciente concentra
alta, atualização cadastral e histórico; Psicologo concentra sua desativação;
Sessao concentra presença e evolução. Contato, Endereco e Responsavel representam
valores sem identidade própria.

Os serviços de aplicação coordenam os casos de uso e delimitam suas transações.
As validações que consultam outros cadastros ficam nessa camada e usam interfaces
de repositório. Controllers cuidam dos contratos HTTP e delegam à aplicação.

As interfaces em `application.port` são implementadas pelos proxies dos repositórios
em `infrastructure.persistence`. Assim, a aplicação não importa Spring Data JPA
nem conhece as consultas SQL/JPQL. Consultas de relatório podem retornar projeções
da aplicação; consultas de pacientes retornam entidades, convertidas em DTOs
dentro da transação.

### Decisões de compatibilidade

- As entidades mantêm anotações JPA e Lombok. Esta é uma implementação DDD em
  camadas com modelo persistente compartilhado, sem duplicar entidades de banco.
- Os contratos de aplicação mantêm `Page`, `Pageable` e `Sort` para preservar a
  paginação existente. O domínio não depende desses tipos.
- Os nomes das tabelas, relacionamentos e rotas existentes foram mantidos.
- Os controllers de psicólogos e sessões continuam sendo os placeholders do
  projeto; a reorganização não cria novas rotas.
- A relação existente entre paciente e sessões, inclusive a exclusão em cascata,
  foi preservada. A separação em agregados com referências apenas por ID e a
  remoção completa de JPA do domínio exigiriam uma mudança adicional de modelo.

## Executar e testar

No PowerShell, configure o JDK 17 instalado:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

O teste de arquitetura protege a direção das dependências. Os testes de domínio
cobrem alta, atualização do valor e presença/evolução. Os testes de aplicação
cobrem a validação do psicólogo ativo. O teste HTTP exercita cadastro, busca,
atualização persistida e alta com H2 e Open Session in View desabilitado.

## CI e gates de qualidade

O workflow `.github/workflows/ci.yml` executa em pull requests, pushes para `main`
e manualmente pelo GitHub Actions. O check **Quality Gate** exige:

- Java 17 e Maven 3.9 ou superior, verificados pelo Maven Enforcer.
- Compilação e empacotamento do JAR sem erros.
- Todos os testes aprovados, incluindo integração HTTP e arquitetura DDD.
- Pelo menos 80% de cobertura de linhas e 60% de branches em **cada pacote de
  domínio**, verificados pelo JaCoCo na fase `verify`.
- Relatórios de testes, arquitetura e cobertura presentes.

O relatório JaCoCo mostra todas as camadas, mas o limite obrigatório se aplica
ao domínio. Código gerado pelo Lombok é identificado para não distorcer a
cobertura. Os testes adicionais cobrem atualização parcial, preservação de
valores e desativação de psicólogos.

O CI usa o perfil `test`, com H2 em memória e sem credenciais externas. O perfil
`prd` permanece reservado para uso futuro. Não há deploy neste workflow.

Para executar os mesmos gates no PowerShell:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:SPRING_PROFILES_ACTIVE = 'test'
.\mvnw.cmd --batch-mode --no-transfer-progress clean verify
```

Relatórios locais: `target/surefire-reports/` e `target/site/jacoco/index.html`.
No Actions, os relatórios ficam disponíveis por 14 dias, inclusive em caso de
falha; o JAR é disponibilizado por 7 dias apenas após aprovação dos gates.

Para impedir merges com falha, configure **Quality Gate** como check obrigatório
na proteção da branch `main` ou em um ruleset do GitHub. O arquivo do workflow
cria o check, mas não altera as regras do repositório remoto.
