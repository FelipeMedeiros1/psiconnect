package br.com.psiconnect.consultorio;

import br.com.psiconnect.consultorio.domain.consulta.AgendaConsultas;
import br.com.psiconnect.consultorio.domain.consulta.AgendamentoSessao;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgendamentoSessaoTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2030-01-01T10:00:00Z"), ZoneOffset.UTC);
    private static final LocalDateTime DATA = LocalDateTime.now(CLOCK).plusDays(1);
    private final AgendaConsultas agenda = mock(AgendaConsultas.class);
    private final AgendamentoSessao agendamento = new AgendamentoSessao(agenda, CLOCK);

    private Paciente paciente() {
        return new Paciente(null, "Ana", LocalDate.of(1990, 1, 1), "Professora", "12345678901", null, null);
    }

    private Psicologo psicologo(long id) {
        var psicologo = spy(new Psicologo("Psicólogo " + id, "123456", Especialidade.ADULTO, null, null));
        doReturn(id).when(psicologo).getId();
        return psicologo;
    }

    @Test
    void primeiraSessaoRecebeValorDefinidoENaoMudaComReajusteDoPaciente() {
        var paciente = paciente();

        var sessao = agendamento.agendar(paciente, psicologo(1), null, DATA, new BigDecimal("150.00"));

        assertThat(sessao.getValorSessao()).isEqualByComparingTo("150.00");
        assertThat(paciente.getValorSessao()).isEqualByComparingTo("150.00");
        assertThat(sessao.getPaciente()).isSameAs(paciente);
        assertThat(sessao.getData()).isEqualTo(DATA);
        assertThat(sessao.isCompareceu()).isFalse();
        paciente.definirValorSessao(new BigDecimal("200.00"));
        assertThat(sessao.getValorSessao()).isEqualByComparingTo("150.00");
    }

    @Test
    void usaValorJaAcordadoMesmoComOutroValorInformado() {
        var paciente = paciente();
        paciente.definirValorSessao(new BigDecimal("120.00"));

        assertThat(agendamento.agendar(paciente, psicologo(1), null, DATA, new BigDecimal("999.00")).getValorSessao())
                .isEqualByComparingTo("120.00");
        assertThat(agendamento.agendar(paciente, psicologo(1), null, DATA, null).getValorSessao())
                .isEqualByComparingTo("120.00");
    }

    @Test
    void permiteSessaoGratuitaQuandoZeroEhInformado() {
        assertThat(agendamento.agendar(paciente(), psicologo(1), null, DATA, BigDecimal.ZERO).getValorSessao())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void exigeValorQuandoPacienteAindaNaoPossuiValorDefinido() {
        var paciente = paciente();
        assertThatThrownBy(() -> agendamento.agendar(paciente, psicologo(1), null, DATA, null))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("valor");
        assertThat(paciente.getValorSessao()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void rejeitaValorNegativoMesmoQuandoExisteValorAcordado() {
        var paciente = paciente();
        paciente.definirValorSessao(new BigDecimal("120.00"));
        assertThatThrownBy(() -> agendamento.agendar(paciente, psicologo(1), null, DATA, new BigDecimal("-1")))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("negativo");
        assertThat(paciente.getValorSessao()).isEqualByComparingTo("120.00");
    }

    @Test
    void pacienteDeAltaNaoPodeAgendarNemAlterarValor() {
        var paciente = paciente();
        paciente.altaPaciente("Concluído");
        assertThatThrownBy(() -> agendamento.agendar(paciente, psicologo(1), null, DATA, BigDecimal.TEN))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("Paciente de alta");
        assertThat(paciente.getValorSessao()).isEqualByComparingTo(BigDecimal.ZERO);
        verifyNoInteractions(agenda);
    }

    @Test
    void rejeitaPsicologoInativoInformadoExplicitamente() {
        var psicologo = psicologo(1);
        psicologo.desativar();
        assertThatThrownBy(() -> agendamento.agendar(paciente(), psicologo, null, DATA, BigDecimal.TEN))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("inativo");
        verifyNoInteractions(agenda);
    }

    @Test
    void conflitoDeHorarioNaoAlteraValorDoPaciente() {
        var paciente = paciente();
        when(agenda.horarioOcupado(1L, DATA)).thenReturn(true);
        assertThatThrownBy(() -> agendamento.agendar(paciente, psicologo(1), null, DATA, BigDecimal.TEN))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("mesmo horário");
        assertThat(paciente.getValorSessao()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(agenda).horarioOcupado(1L, DATA);
    }

    @Test
    void selecaoAutomaticaIgnoraInativosOcupadosEOutraEspecialidade() {
        var inativo = psicologo(1);
        inativo.desativar();
        var outraEspecialidade = new Psicologo("Outro", "123457", Especialidade.CASAL, null, null);
        var ocupado = psicologo(2);
        var livre = psicologo(3);
        when(agenda.psicologosDaEspecialidade(Especialidade.ADULTO))
                .thenReturn(List.of(inativo, outraEspecialidade, ocupado, livre));
        when(agenda.horarioOcupado(2L, DATA)).thenReturn(true);

        var sessao = agendamento.agendar(paciente(), null, Especialidade.ADULTO, DATA, BigDecimal.TEN);

        assertThat(sessao.getPsicologo()).isSameAs(livre);
        verify(agenda, never()).horarioOcupado(1L, DATA);
        verify(agenda).horarioOcupado(3L, DATA);
    }

    @Test
    void selecaoAutomaticaSemDisponibilidadeRejeitaAgendamento() {
        var ocupado = psicologo(1);
        when(agenda.psicologosDaEspecialidade(Especialidade.ADULTO)).thenReturn(List.of(ocupado));
        when(agenda.horarioOcupado(1L, DATA)).thenReturn(true);
        assertThatThrownBy(() -> agendamento.agendar(paciente(), null, Especialidade.ADULTO, DATA, BigDecimal.TEN))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("Não existe psicólogo disponível");
    }

    @Test
    void selecaoAutomaticaExigeEspecialidade() {
        assertThatThrownBy(() -> agendamento.agendar(paciente(), null, null, DATA, BigDecimal.TEN))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("especialidade");
        verifyNoInteractions(agenda);
    }

    static Stream<LocalDateTime> datasInvalidas() {
        return Stream.of(null, LocalDateTime.now(CLOCK), LocalDateTime.now(CLOCK).minusSeconds(1));
    }

    @ParameterizedTest
    @MethodSource("datasInvalidas")
    void exigeDataFuturaMesmoSemValidacaoHttp(LocalDateTime data) {
        assertThatThrownBy(() -> agendamento.agendar(paciente(), psicologo(1), null, data, BigDecimal.TEN))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("futuro");
        verifyNoInteractions(agenda);
    }

    @Test
    void exigePaciente() {
        assertThatThrownBy(() -> agendamento.agendar(null, psicologo(1), null, DATA, BigDecimal.TEN))
                .isInstanceOf(ConsultorioException.class).hasMessageContaining("Paciente");
        verifyNoInteractions(agenda);
    }
}
