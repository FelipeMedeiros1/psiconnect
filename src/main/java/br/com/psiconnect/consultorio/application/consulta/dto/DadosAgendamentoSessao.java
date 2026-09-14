package br.com.psiconnect.consultorio.application.consulta.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DadosAgendamentoSessao(
        Long idPsicologo,
        @NotNull
        Long idPaciente,
        @NotNull
        @Future LocalDateTime data,
        Especialidade especialidade,
        @NotNull @DecimalMin(value = "0.01") BigDecimal valorSessao
) {
}
