package br.com.psiconnect.consultorio.consulta.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosAtualizacaoValorSessao(
        @NotNull
        Long idPaciente,
        @NotNull
        BigDecimal valorSessao,
        @NotNull
        LocalDate inicioMes,
        @NotNull
        LocalDate fimMes
) {
}
