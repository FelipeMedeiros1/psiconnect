package br.com.psiconnect.consultorio.infrastructure.configuration;

import br.com.psiconnect.consultorio.application.port.PacienteRepository;
import br.com.psiconnect.consultorio.application.port.PsicologoRepository;
import br.com.psiconnect.consultorio.application.port.SessaoRepository;
import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import br.com.psiconnect.consultorio.domain.paciente.Paciente;
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
        criarPsicologo("Sigmund Freud", "10001", Especialidade.ADULTO, "freud@demo.com");
        criarPsicologo("Carl Gustav Jung", "10002", Especialidade.ADULTO, "jung@demo.com");
        criarPsicologo("Burrhus Frederic Skinner", "10003", Especialidade.ADULTO, "skinner@demo.com");
        criarPsicologo("Jean Piaget", "10004", Especialidade.INFANTIL, "piaget@demo.com");
        criarPsicologo("Carl Ransom Rogers", "10005", Especialidade.FAMILIAR, "rogers@demo.com");

        criarPaciente("55566677788", "Anna Freud", LocalDate.of(1895, 12, 3), "Psicanalista", "7", "anna.freud@demo.com");
        criarPaciente("12345678912", "Marie-Louise von Franz", LocalDate.of(1915, 1, 4), "Psicóloga", "1", "vonfranz@demo.com");
        criarPaciente("98765432100", "Ogden Lindsley", LocalDate.of(1922, 8, 11), "Psicólogo", "2", "lindsley@demo.com");
        criarPaciente("74185296300", "Bärbel Inhelder", LocalDate.of(1913, 4, 15), "Psicóloga", "3", "inhelder@demo.com");
        criarPaciente("36925814700", "Thomas Gordon", LocalDate.of(1918, 3, 11), "Psicólogo", "4", "gordon@demo.com");
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
        var existente = psicologos.findAll().stream()
                .filter(psicologo -> crp.equals(psicologo.getCrp()))
                .findFirst();
        if (existente.isPresent()) {
            existente.get().atualizarInformacoes(nome, new Contato("11999990000", email), endereco("10"));
            psicologos.save(existente.get());
            return;
        }
        psicologos.save(new Psicologo(
                nome,
                crp,
                especialidade,
                new Contato("11999990000", email),
                endereco("10")));
    }

    private void criarPaciente(String cpf, String nome, LocalDate nascimento, String profissao,
                               String numeroEndereco, String email) {
        var existente = pacientes.findAll().stream()
                .filter(paciente -> cpf.equals(paciente.getCpf()))
                .findFirst();
        if (existente.isPresent()) {
            existente.get().atualizarInformacoes(nome, null, new Contato("11955550000", email), endereco(numeroEndereco));
            pacientes.save(existente.get());
            return;
        }
        pacientes.save(new Paciente(
                null,
                nome,
                nascimento,
                profissao,
                cpf,
                endereco(numeroEndereco),
                new Contato("11955550000", email)));
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
