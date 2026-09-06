package br.com.psiconnect.consultorio;

import br.com.psiconnect.consultorio.application.consulta.SessaoService;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;
import br.com.psiconnect.consultorio.application.port.PacienteRepository;
import br.com.psiconnect.consultorio.application.port.PsicologoRepository;
import br.com.psiconnect.consultorio.application.port.SessaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:agendamento-test;DB_CLOSE_DELAY=-1")
@ActiveProfiles("test")
class AgendamentoIntegrationTest {
    private static final LocalDateTime DATA = LocalDateTime.of(2030, 1, 2, 10, 0);

    @TestConfiguration
    static class RelogioDeTeste {
        @Bean
        @Primary
        Clock relogioFixo() {
            return Clock.fixed(Instant.parse("2030-01-01T10:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired private SessaoService service;
    @Autowired private PacienteRepository pacientes;
    @Autowired private PsicologoRepository psicologos;
    @Autowired private SessaoRepository sessoes;

    @BeforeEach
    void limparDados() {
        sessoes.findAll().forEach(sessao -> sessoes.deleteById(sessao.getId()));
        pacientes.findAll().forEach(paciente -> pacientes.deleteById(paciente.getId()));
        psicologos.findAll().forEach(psicologo -> psicologos.deleteById(psicologo.getId()));
    }

    private Paciente paciente(String cpf) {
        return pacientes.save(new Paciente(null, "Ana", LocalDate.of(1990, 1, 1), "Professora", cpf, null, null));
    }

    private Psicologo psicologo(String crp) {
        return psicologos.save(new Psicologo("Psicólogo", crp, Especialidade.ADULTO, null, null));
    }

    @Test
    void primeiroAgendamentoPersisteValorCorretoEPreservaHistoricoAposReajuste() {
        var paciente = paciente("12345678901");
        var psicologo = psicologo("123456");
        var primeira = service.agendar(new DadosAgendamentoSessao(psicologo.getId(), paciente.getId(), DATA,
                null, new BigDecimal("150.00")));

        assertThat(sessoes.findById(primeira.id()).orElseThrow().getValorSessao()).isEqualByComparingTo("150.00");
        var atualizado = pacientes.findById(paciente.getId()).orElseThrow();
        assertThat(atualizado.getValorSessao()).isEqualByComparingTo("150.00");
        atualizado.definirValorSessao(new BigDecimal("200.00"));
        pacientes.save(atualizado);

        var segunda = service.agendar(new DadosAgendamentoSessao(psicologo.getId(), paciente.getId(), DATA.plusDays(1),
                null, null));

        assertThat(sessoes.findById(primeira.id()).orElseThrow().getValorSessao()).isEqualByComparingTo("150.00");
        assertThat(sessoes.findById(segunda.id()).orElseThrow().getValorSessao()).isEqualByComparingTo("200.00");
    }

    @Test
    void conflitoNaoGravaOutraSessaoNemAlteraValorDoPaciente() {
        var primeiro = paciente("12345678901");
        var segundo = paciente("12345678902");
        var psicologo = psicologo("123456");
        service.agendar(new DadosAgendamentoSessao(psicologo.getId(), primeiro.getId(), DATA, null, BigDecimal.TEN));

        assertThatThrownBy(() -> service.agendar(new DadosAgendamentoSessao(psicologo.getId(), segundo.getId(),
                DATA, null, new BigDecimal("150.00"))))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("mesmo horário");

        assertThat(sessoes.findAll()).hasSize(1);
        assertThat(pacientes.findById(segundo.getId()).orElseThrow().getValorSessao()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void selecaoAutomaticaUsaOutroPsicologoQuandoHaInativoEOcupado() {
        var paciente = paciente("12345678901");
        var inativo = psicologo("123456");
        inativo.desativar();
        psicologos.save(inativo);
        var ocupado = psicologo("123457");
        var livre = psicologo("123458");
        service.agendar(new DadosAgendamentoSessao(ocupado.getId(), paciente.getId(), DATA, null, BigDecimal.TEN));

        var agendada = service.agendar(new DadosAgendamentoSessao(null, paciente.getId(), DATA,
                Especialidade.ADULTO, null));

        assertThat(agendada.idPsicologo()).isEqualTo(livre.getId());
        assertThat(sessoes.findAll()).hasSize(2);
    }

    @Test
    void cadastrosInexistentesSaoRejeitadosSemPersistirSessao() {
        assertThatThrownBy(() -> service.agendar(new DadosAgendamentoSessao(null, -1L, DATA,
                Especialidade.ADULTO, BigDecimal.TEN)))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("paciente informado não existe");
        var paciente = paciente("12345678901");
        assertThatThrownBy(() -> service.agendar(new DadosAgendamentoSessao(-1L, paciente.getId(), DATA,
                null, BigDecimal.TEN)))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("psicólogo informado não existe");
        assertThat(sessoes.findAll()).isEmpty();
    }
}
