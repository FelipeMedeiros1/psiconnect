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
        if (data == null || !data.isAfter(LocalDateTime.now(clock))) {
            throw new ConsultorioException("A data da sessão deve estar no futuro!");
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

        // Somente altera o valor do paciente depois de todas as validações da agenda.
        BigDecimal valor = paciente.definirValorParaAgendamento(valorInformado);
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
