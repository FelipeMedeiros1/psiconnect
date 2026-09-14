package br.com.psiconnect.consultorio.infrastructure.configuration;

import br.com.psiconnect.consultorio.application.port.PacienteRepository;
import br.com.psiconnect.consultorio.application.port.PsicologoRepository;
import br.com.psiconnect.consultorio.application.port.SessaoRepository;
import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.paciente.Responsavel;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;
import br.com.psiconnect.consultorio.domain.consulta.Sessao;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {
    private final PsicologoRepository psicologos;
    private final PacienteRepository pacientes;
    private final SessaoRepository sessoes;

    public DevDataSeeder(PsicologoRepository psicologos, PacienteRepository pacientes, SessaoRepository sessoes) {
        this.psicologos = psicologos;
        this.pacientes = pacientes;
        this.sessoes = sessoes;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        criarPsicologo("Viviane Teste", "10001", Especialidade.ADULTO, "viviane@teste.com");
        criarPsicologo("Marina Teste", "10002", Especialidade.INFANTIL, "marina@teste.com");
        criarPsicologo("Carlos Teste", "10003", Especialidade.FAMILIAR, "carlos@teste.com");

        if (!pacientes.existsByCpf("12345678912")) {
            pacientes.save(new Paciente(
                    null,
                    "Paciente Adulto Teste",
                    LocalDate.of(1983, 7, 23),
                    "QA",
                    "12345678912",
                    endereco("1"),
                    new Contato("11312345678", "adulto@teste.com")));
        }

        if (!pacientes.existsByCpf("98765432100")) {
            pacientes.save(new Paciente(
                    new Responsavel("Responsável Teste", "11122233344"),
                    "Paciente Menor Teste",
                    LocalDate.now().minusYears(10),
                    "Estudante",
                    "98765432100",
                    endereco("2"),
                    new Contato("11987654321", "menor@teste.com")));
        }


        if (!pacientes.existsByCpf("55566677788")) {
            pacientes.save(new Paciente(
                    null,
                    "Paciente Histórico Teste",
                    LocalDate.of(1990, 5, 15),
                    "Analista",
                    "55566677788",
                    endereco("7"),
                    new Contato("11955556666", "historico@teste.com")));
        }

        var pacienteHistorico = pacientes.findAll().stream()
                .filter(paciente -> "55566677788".equals(paciente.getCpf()))
                .findFirst().orElseThrow();
        var psicologoHistorico = psicologos.findAll().stream()
                .filter(psicologo -> "10001".equals(psicologo.getCrp()))
                .findFirst().orElseThrow();
        boolean atendimentoJaCriado = sessoes.findAll().stream()
                .anyMatch(sessao -> sessao.getPaciente().getId().equals(pacienteHistorico.getId()) && sessao.isCompareceu());
        if (!atendimentoJaCriado) {
            sessoes.save(Sessao.atendimentoRealizado(
                    LocalDateTime.now().minusDays(7),
                    pacienteHistorico,
                    psicologoHistorico,
                    new BigDecimal("250.00"),
                    "Atendimento de teste realizado há sete dias."));
            pacientes.save(pacienteHistorico);
        }
    }

    private void criarPsicologo(String nome, String crp, Especialidade especialidade, String email) {
        if (!psicologos.existsByCrp(crp)) {
            psicologos.save(new Psicologo(
                    nome,
                    crp,
                    especialidade,
                    new Contato("11999990000", email),
                    endereco("10")));
        }
    }

    private Endereco endereco(String numero) {
        return new Endereco(
                "Rua Judith Zumkeller",
                "Parque Mandaqui",
                "02422020",
                numero,
                "",
                "São Paulo",
                "SP");
    }
}
