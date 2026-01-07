package br.com.psiconnect.consultorio.consulta.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DadosAtualizacaoSessao(
        @NotNull
        Long id,
        Long idPsicologo,
        Long idPaciente,
        LocalDateTime data
) {
}
