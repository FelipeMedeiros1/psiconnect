# PsiConnect

## Logs e rastreabilidade

Cada requisicao recebe um UUID gerado pelo servidor no header `X-Request-ID`.
Use esse valor para localizar `requestId` nos logs do console. Um `operationId`
identifica cada chamada publica aos services, inclusive fora de requisicoes HTTP.
Os registros usam campos `event`, `operation`, `route`, `status` e `durationMs`.

- `INFO`: inicio/fim de requisicoes e operacoes; conclusao de transacoes externas.
- `WARN`: respostas 4xx, validacoes, regras de negocio e operacoes com falha.
- `ERROR`: respostas 5xx e erros inesperados, com tipo e frames da excecao no tratador.
- `DEBUG`: operacao aguardando confirmacao de uma transacao externa.

O aspecto envolve o interceptor transacional: `operation_completed` so aparece
apos seu retorno, incluindo o commit. Se houver transacao externa, o evento
`operation_transaction_completed` informa `committed`, `rolled_back` ou `unknown`
e mantem os identificadores em `operationRef` e `requestRef`.
Chamadas internas do mesmo service nao geram outra operacao por usarem o mesmo proxy.

Os logs HTTP usam o modelo da rota (ex.: `/pacientes/nome/{nome}`), nunca a URL
completa, query string, corpo ou headers recebidos. Argumentos, retornos e mensagens
de excecao nao sao registrados, pois podem conter dados pessoais ou clinicos.
SQL e binding de parametros ficam desativados por padrao, inclusive em dev.
Configure `APP_LOG_LEVEL` para ajustar o nivel da aplicacao (padrao `INFO`).
Os logs vao para o console; retencao e acesso devem ser configurados no coletor
do ambiente. Estes registros operacionais nao constituem uma trilha de auditoria
de autoria: o projeto ainda nao fornece identidade autenticada nos fluxos.

Aplicação Spring Boot / Java 17 organizada pelo contexto de negócio **Consultório**.

## Arquitetura DDD

```text
br.com.psiconnect.consultorio
├── domain
│   ├── paciente       # Paciente e Responsavel
│   ├── psicologo      # Psicologo e Especialidade
│   ├── consulta       # Sessao, AgendamentoSessao e contrato AgendaConsultas
│   ├── contato        # Objeto de valor Contato
│   ├── endereco       # Objeto de valor Endereco
│   └── exception      # Exceções de negócio
├── application
│   ├── paciente       # Casos de uso e DTOs
│   ├── psicologo      # Casos de uso e DTOs
│   ├── consulta       # Coordenação do agendamento, relatórios e DTOs
│   ├── contato, endereco # DTOs compartilhados
│   ├── mapper         # Conversão dos DTOs em valores de domínio
│   └── port           # Contratos de acesso à persistência
└── infrastructure
    ├── configuration  # Instanciação dos serviços de domínio e Clock
    ├── persistence    # Implementações Spring Data JPA e consultas JPQL
    └── web            # Controllers e tradução de erros para HTTP
```

O domínio não importa aplicação, infraestrutura, Spring ou DTOs. Paciente concentra
alta, atualização cadastral e histórico; Psicologo concentra sua desativação;
Sessao concentra presença e evolução. Contato, Endereco e Responsavel representam
valores sem identidade própria.

Os serviços de aplicação coordenam os casos de uso e delimitam suas transações.
No agendamento, a aplicação carrega paciente e psicólogo, chama o domínio e
persiste o resultado. Controllers cuidam dos contratos HTTP e delegam à aplicação.

`domain.consulta.AgendamentoSessao` concentra a validação de data futura,
disponibilidade e seleção automática do psicólogo. Paciente e Psicologo validam
se estão ativos. O domínio consulta a agenda pelo contrato `AgendaConsultas`,
implementado por `AgendaConsultasJpa`, e recebe um `Clock` para controlar o tempo
nos testes. Nenhum desses contratos de domínio depende de Spring ou DTOs.

Sessao só é criada pelo serviço de domínio, com preço definido na criação; seus
construtores não são públicos e não há alteração pública do preço. O valor já
acordado com o paciente prevalece. Quando o valor atual é zero, o agendamento
exige um valor informado, que pode ser zero para uma sessão gratuita. Valores
negativos são rejeitados. O primeiro agendamento grava o mesmo preço no paciente
e na sessão; reajustes posteriores não alteram sessões anteriores.

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

Para subir a aplicação localmente no **PowerShell** (terminal iniciado por `PS`),
abra dois terminais.

No primeiro terminal, inicie o back-end:

```powershell
cd C:\projetos\psiconnect
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:SPRING_PROFILES_ACTIVE = 'dev'
.\mvnw.cmd spring-boot:run
```

No segundo terminal, instale as dependências (na primeira execução) e inicie o web:

```powershell
cd C:\projetos\Psiconnect-web
npm.cmd install
npm.cmd start
```

O back-end ficará disponível em `http://localhost:8080` e o web em
`http://localhost:4200`.

Se o terminal for realmente o **Git Bash** (prompt terminado em `$`), use:

```bash
cd /c/projetos/psiconnect
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

O projeto exige Java 17; iniciar o Maven Wrapper sem ajustar `JAVA_HOME` pode
fazer a aplicação usar outra versão do Java instalada na máquina.

No PowerShell, configure o JDK 17 instalado:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

O teste de arquitetura protege a direção das dependências. Os testes de domínio
cobrem alta, preço, presença/evolução e agendamento sem contexto Spring. Os testes
de integração cobrem persistência do preço, conflitos de horário e seleção automática.
O teste HTTP exercita cadastro, busca,
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
