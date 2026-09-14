package br.com.psiconnect.consultorio.application.consulta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosRegistroAtendimento(
        @NotBlank
        @Size(max = 600, message = "A evolução deve ter no máximo 600 caracteres")
        String informacoes) {
}
