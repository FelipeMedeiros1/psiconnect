package br.com.psiconnect.consultorio.consulta.dto;

import jakarta.validation.constraints.NotNull;

public record DadosCancelamentoSessao(
        @NotNull
        Boolean cobravel,
        @NotNull
        Boolean reagendada
) {
}
