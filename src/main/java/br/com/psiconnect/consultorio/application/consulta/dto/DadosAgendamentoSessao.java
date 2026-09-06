package br.com.psiconnect.consultorio.application.consulta.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DadosAgendamentoSessao(
        @NotNull
        Long idPsicologo,
        @NotNull
        Long idPaciente,
        @NotNull
        @Future
        LocalDateTime data,
        Especialidade especialidade,
        BigDecimal valorSessao
) {
}