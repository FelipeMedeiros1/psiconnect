package br.com.psiconnect.consultorio.domain.consulta;

import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

public final class AgendamentoSessao {
    private final AgendaConsultas agenda;
    private final Clock clock;

    public AgendamentoSessao(AgendaConsultas agenda, Clock clock) {
        this.agenda = Objects.requireNonNull(agenda);
        this.clock = Objects.requireNonNull(clock);
    }

    public Sessao agendar(Paciente paciente, Psicologo psicologo, Especialidade especialidade,
                         LocalDateTime data, BigDecimal valorInformado) {
        if (paciente == null) {
            throw new ConsultorioException("Paciente deve ser informado para agendar uma sessão!");
        }
        if (data == null) {
            throw new ConsultorioException("A data da sessão deve ser informada!");
        }
        if (data.toLocalDate().isBefore(LocalDateTime.now(clock).toLocalDate())) {
            throw new ConsultorioException("A data da consulta não pode ser anterior à data atual!");
        }
        if (!data.isAfter(LocalDateTime.now(clock))) {
            throw new ConsultorioException("O horário da consulta não pode ser anterior à hora atual!");
        }
        if (data.toLocalTime().isBefore(java.time.LocalTime.of(7, 0))
                || data.toLocalTime().isAfter(java.time.LocalTime.of(22, 0))) {
            throw new ConsultorioException("O horário da consulta deve estar entre 07:00 e 22:00!");
        }
        paciente.validarAgendamento();
        Psicologo escolhido;
        if (psicologo == null) {
            escolhido = escolherPsicologo(especialidade, data);
        } else {
            psicologo.validarAgendamento();
            if (agenda.horarioOcupado(psicologo.getId(), data)) {
                throw new ConsultorioException("Psicólogo já possui outra consulta agendada nesse mesmo horário");
            }
            escolhido = psicologo;
        }

        if (agenda.pacienteIndisponivel(paciente.getId(), data)) {
            throw new ConsultorioException("Paciente já possui uma consulta em um intervalo menor que 50 minutos!");
        }

        // Somente altera o valor do paciente depois de todas as validações da agenda.
        BigDecimal valor = paciente.definirValorParaAgendamento(valorInformado);
        if (valor == null || valor.signum() <= 0) {
            throw new ConsultorioException("O valor da sessão deve ser maior que zero!");
        }
        return new Sessao(data, paciente, escolhido, valor);
    }

    private Psicologo escolherPsicologo(Especialidade especialidade, LocalDateTime data) {
        if (especialidade == null) {
            throw new ConsultorioException("Informe a especialidade para escolher um psicólogo automaticamente!");
        }
        return agenda.psicologosDaEspecialidade(especialidade).stream()
                .filter(psicologo -> Boolean.TRUE.equals(psicologo.getAtivo()))
                .filter(psicologo -> psicologo.getEspecialidade() == especialidade)
                .filter(psicologo -> !agenda.horarioOcupado(psicologo.getId(), data))
                .findFirst()
                .orElseThrow(() -> new ConsultorioException("Não existe psicólogo disponível nessa data!"));
    }
}
