package br.com.psiconnect.consultorio.application.consulta.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DadosAtualizacaoSessao(
        @NotNull Long id,
        @NotNull Long idPsicologo,
        @NotNull Long idPaciente,
        @NotNull @Future LocalDateTime data,
        @NotNull @DecimalMin(value = "0.01") BigDecimal valorSessao) {
}
